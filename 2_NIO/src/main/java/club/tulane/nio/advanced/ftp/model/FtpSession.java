package club.tulane.nio.advanced.ftp.model;

import club.tulane.nio.advanced.ftp.FtpPortDataClient;
import club.tulane.nio.advanced.ftp.command.Command;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class FtpSession {

    private static final AttributeKey<FtpSession> SESSION_KEY = AttributeKey.valueOf("ftp.session");

    private ChannelHandlerContext ctx;

    private boolean loggedIn;

    private FileSystemView fileSystemView;

    private FtpPortDataClient portDataClient;

    private String uploadFilePath;

    /**
     * 前一个指令记录: 用于识别是 主动/被动 模式
     */
    private Command preCommand;

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
