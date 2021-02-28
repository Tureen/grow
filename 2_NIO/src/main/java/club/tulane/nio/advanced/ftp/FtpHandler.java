package club.tulane.nio.advanced.ftp;

import club.tulane.nio.advanced.ftp.command.Command;
import club.tulane.nio.advanced.ftp.command.impl.LIST;
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
        final Command command = request.getCommand();
        if(command == null){
            FtpResponse response = new FtpResponse(FtpReply.REPLY_502);
            sendResponse(response, ctx);
            return;
        }
        if(command instanceof LIST){
            session.getPortDataClient().connect().addListener(future -> {
                final FtpResponse response = command.execute(request, session);
                sendResponse(response, ctx);
            });
            return;
        }
        final FtpResponse response = command.execute(request, session);
        sendResponse(response, ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        FtpSession.getOrCreateSession(ctx);
        sendResponse(new FtpResponse(FtpReply.REPLY_220), ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final FtpSession session = FtpSession.getOrCreateSession(ctx);
        log.info("session[{}] lost connection", session);
        if(session.isLoggedIn()){
            session.setLoggedIn(false);
        }
    }

    public static ChannelFuture sendResponse(FtpResponse response, ChannelHandlerContext ctx) {
        return ctx.writeAndFlush(response).addListener(future -> {
            final ChannelPromise channelPromise = response.getChannelPromise();
            if(channelPromise != null) {
                channelPromise.setSuccess();
            }
        });
    }
}
