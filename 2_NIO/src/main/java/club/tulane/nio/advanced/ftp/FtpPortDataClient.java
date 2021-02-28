package club.tulane.nio.advanced.ftp;

import club.tulane.nio.advanced.ftp.model.FtpSession;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.function.Function;

/**
 * 主动传输模式的连接
 */
@Getter
@Setter
@Slf4j
public class FtpPortDataClient {

    private FtpSession session;

    private InetSocketAddress address;

    private Bootstrap bootstrap;

    private ChannelFuture channelFuture;

    public FtpPortDataClient(FtpSession session, InetSocketAddress address) {
        this.session = session;
        this.address = address;
        bootstrap = new Bootstrap();
    }

    public ChannelFuture connect() {
        EventLoopGroup eventLoopGroup = session.getCtx().channel().eventLoop();
        this.channelFuture = bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new IdleStateHandler(0, 0, 120));
                    }
                }).connect(address).addListener(future -> {
                            log.info("port data client[{}] of session[{}] is connected", address, session);
                        }
                );
        return this.channelFuture;
    }

    public Promise<Void> writeAndFlushData(Function<Channel, ChannelFuture> function){
        Promise<Void> promise = session.getCtx().channel().eventLoop().newPromise();
        function.apply(this.channelFuture.channel()).addListener(future -> {
            promise.setSuccess(null);
        });
        return promise;
    }

    public Promise<Void> close(){
        Promise<Void> promise = session.getCtx().channel().eventLoop().newPromise();
        channelFuture.channel().close().addListener(future -> {
            log.info("port data client[{}] of session[{}] is closed", this, session);
            promise.setSuccess(null);
        });
        return promise;
    }
}
