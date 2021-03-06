package club.tulane.nio.advanced.ftp.command.impl;

import club.tulane.nio.advanced.ftp.FtpHandler;
import club.tulane.nio.advanced.ftp.FtpReply;
import club.tulane.nio.advanced.ftp.command.Command;
import club.tulane.nio.advanced.ftp.model.FtpRequest;
import club.tulane.nio.advanced.ftp.model.FtpResponse;
import club.tulane.nio.advanced.ftp.model.FtpSession;

public class STOR implements Command {

    @Override
    public FtpResponse execute(FtpRequest request, FtpSession session) {
        session.setUploadFilePath(request.getArgument());

        final FtpResponse response = new FtpResponse(FtpReply.REPLY_150);
        // 设置 Promise 标识对象, 供通信端口返回成功响应后回调
        response.setChannelPromise(
                // 包装一个 Promise 对象, 实际是触发 Promise 被通知时的监听函数
                session.getCtx().newPromise().addListener(future -> {
                    // 配置监听函数: 发送文件
                    doReceiveFileContent(session);
                })
        );
        return response;
    }

    private void doReceiveFileContent(FtpSession session) {
        session.getPortDataClient().getChannelFuture().channel().closeFuture().addListener(future -> {
            FtpHandler.sendResponse(new FtpResponse(FtpReply.REPLY_226), session.getCtx());
        });
    }
}
