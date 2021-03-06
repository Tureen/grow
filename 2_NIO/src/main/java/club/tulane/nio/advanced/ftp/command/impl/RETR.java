package club.tulane.nio.advanced.ftp.command.impl;

import club.tulane.nio.advanced.ftp.FtpHandler;
import club.tulane.nio.advanced.ftp.FtpReply;
import club.tulane.nio.advanced.ftp.command.Command;
import club.tulane.nio.advanced.ftp.model.FileView;
import club.tulane.nio.advanced.ftp.model.FtpRequest;
import club.tulane.nio.advanced.ftp.model.FtpResponse;
import club.tulane.nio.advanced.ftp.model.FtpSession;
import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.Promise;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 下载文件
 */
public class RETR implements Command {

    @Override
    public FtpResponse execute(FtpRequest request, FtpSession session) {

        // 获取请求下载的文件
        FileView file = session.getFileSystemView().getFile(request.getArgument());

        if(!file.isFile()){
            return new FtpResponse(FtpReply.REPLY_550);
        }

        final FtpResponse response = new FtpResponse(FtpReply.REPLY_150);
        // 设置 Promise 标识对象, 供通信端口返回成功响应后回调
        response.setChannelPromise(
                // 包装一个 Promise 对象, 实际是触发 Promise 被通知时的监听函数
                session.getCtx().newPromise().addListener(future -> {
                    // 配置监听函数: 发送文件
                    doSendFileContent(session, request, file);
                })
        );
        return response;
    }

    /**
     * 发送文件数据
     * @param session
     * @param request
     * @param file
     * @throws IOException
     */
    private void doSendFileContent(FtpSession session, FtpRequest request, FileView file) throws IOException {
        // 获取随机读写文件流, 将数据传入 byte数组
        final RandomAccessFile accessFile = new RandomAccessFile(file.getFile(), "r");
        final byte[] bytes = new byte[(int) accessFile.length()];
        accessFile.read(bytes);

        // 文件流的字节数组写入到连接器的通道中
        final Promise<Void> promise = session.getPortDataClient().writeAndFlushData(channel -> {
            ByteBuf buffer = channel.alloc().buffer(bytes.length).writeBytes(bytes);
            return channel.writeAndFlush(buffer).addListener(future -> accessFile.close());
        });

        // 等待写入完成被调用, 返回成功响应并关闭连接
        promise.addListener(future -> {
            FtpHandler.sendResponse(new FtpResponse(FtpReply.REPLY_226), session.getCtx()).addListener(future1 -> {
                session.getPortDataClient().close();
            });
        });
    }
}
