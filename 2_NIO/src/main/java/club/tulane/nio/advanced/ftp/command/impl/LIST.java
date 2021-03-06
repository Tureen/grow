package club.tulane.nio.advanced.ftp.command.impl;

import club.tulane.nio.advanced.ftp.FtpHandler;
import club.tulane.nio.advanced.ftp.FtpReply;
import club.tulane.nio.advanced.ftp.command.Command;
import club.tulane.nio.advanced.ftp.model.*;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * 命令: list
 *
 * 展示当前用户所处ftp服务器目录下的文件列表
 * 由于是借助主动/被动模式, 需要开启新连接做数据传输
 */
@Slf4j
public class LIST implements Command {

    private static final LISTFileFormatter LIST_FILE_FORMATER = new LISTFileFormatter();

    private final DirectoryLister directoryLister = new DirectoryLister();

    @Override
    public FtpResponse execute(FtpRequest request, FtpSession session) {
        final FtpResponse response = new FtpResponse(FtpReply.REPLY_150);

        // 设置 Promise 标识对象, 供通信端口返回成功响应后回调
        response.setChannelPromise(
                // 包装一个 Promise 对象, 实际是触发 Promise 被通知时的监听函数
                session.getCtx().newPromise().addListener(future -> {
                    // 配置监听函数: 发送文件列表
                    doSendFileList(session, request);
                })
        );
        return response;
    }

    /**
     * 发送文件列表
     * @param session
     * @param request
     */
    private void doSendFileList(FtpSession session, FtpRequest request) {
        try {
            // 获得请求目录的文件列表
            ListArgument parsedArg = ListArgument.parse(request.getArgument());
            String content = directoryLister.listFiles(parsedArg, session.getFileSystemView(), LIST_FILE_FORMATER);

            // 转换成 byte 数组
            final byte[] bytes;
            if (StringUtils.isBlank(content)) {
                bytes = " ".getBytes(CharsetUtil.UTF_8);
            }else{
                bytes = content.getBytes(CharsetUtil.UTF_8);
            }

            // 调用主动/被动模式的端口连接器, 向其连接发送数据 (通过传入函数)
            final Promise<Void> promise = session.getPortDataClient().writeAndFlushData(channel -> {
                // 获得连接器的 channel 通道, 传输 byte 数组
                ByteBuf buffer = channel.alloc().buffer(bytes.length).writeBytes(bytes);
                return channel.writeAndFlush(buffer);
            });

            // 监听此 Promise , 这个 Promise 标识传输文件列表的状态, 当数据传输完成时执行函数, 返回成功响应并关闭连接
            promise.addListener(future -> {
                FtpHandler.sendResponse(new FtpResponse(FtpReply.REPLY_226), session.getCtx()).addListener(future1 -> {
                    session.getPortDataClient().close();
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
