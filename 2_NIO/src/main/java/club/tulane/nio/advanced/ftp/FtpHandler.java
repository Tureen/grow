package club.tulane.nio.advanced.ftp;

import club.tulane.nio.advanced.ftp.command.Command;
import club.tulane.nio.advanced.ftp.command.impl.PORT;
import club.tulane.nio.advanced.ftp.model.FtpRequest;
import club.tulane.nio.advanced.ftp.model.FtpResponse;
import club.tulane.nio.advanced.ftp.model.FtpSession;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FtpHandler extends SimpleChannelInboundHandler<FtpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FtpRequest request) {
        final FtpSession session = FtpSession.getOrCreateSession(ctx);

        // 搜索请求中的指令, 没有则返回错误
        final Command command = request.getCommand();
        if(command == null){
            FtpResponse response = new FtpResponse(FtpReply.REPLY_502);
            sendResponse(response, ctx);
            return;
        }

        // 主动\被动模式判断, 如果是则走回调方式
        final Command preCommand = session.getPreCommand();
        if(preCommand instanceof PORT){
            session.setPreCommand(command);
            session.getPortDataClient().connect().addListener(future -> {
                final FtpResponse response = command.execute(request, session);
                sendResponse(response, ctx);
            });
            return;
        }

        session.setPreCommand(command);
        final FtpResponse response = command.execute(request, session);
        sendResponse(response, ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 连接成功 构建 session
        FtpSession.getOrCreateSession(ctx);
        sendResponse(new FtpResponse(FtpReply.REPLY_220), ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        final FtpSession session = FtpSession.getOrCreateSession(ctx);
        log.info("session[{}] lost connection", session);
        // 下线取消 session
        if(session.isLoggedIn()){
            session.setLoggedIn(false);
        }
    }

    public static ChannelFuture sendResponse(FtpResponse response, ChannelHandlerContext ctx) {
        return ctx.writeAndFlush(response).addListener(future -> {
            // 传输完成后, 调用响应对象中的 Promise 标识对象, 通知回调函数处理后续流程
            final ChannelPromise channelPromise = response.getChannelPromise();
            if(channelPromise != null) {
                channelPromise.setSuccess();
            }
        });
    }
}
