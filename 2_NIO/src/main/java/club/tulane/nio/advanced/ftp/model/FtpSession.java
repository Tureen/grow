package club.tulane.nio.advanced.ftp.model;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class FtpSession {

    private static final AttributeKey<FtpSession> SESSION_KEY = AttributeKey.valueOf("ftp.session");

    private ChannelHandlerContext ctx;

    private boolean loggedIn;

    private FileSystemView fileSystemView;

    public FtpSession(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public static FtpSession getOrCreateSession(ChannelHandlerContext ctx){
        final Channel channel = ctx.channel();
        if(!channel.hasAttr(SESSION_KEY)){
            FtpSession newSession = new FtpSession(ctx);
            channel.attr(SESSION_KEY).set(newSession);
            log.info("session[{}] is created", newSession);
        }
        return channel.attr(SESSION_KEY).get();
    }
}
