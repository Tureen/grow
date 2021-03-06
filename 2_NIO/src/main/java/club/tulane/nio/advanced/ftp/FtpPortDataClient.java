package club.tulane.nio.advanced.ftp;

import club.tulane.nio.advanced.ftp.model.FileView;
import club.tulane.nio.advanced.ftp.model.FtpSession;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;
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
                                .addLast(new IdleStateHandler(0, 0, 120))
                                .addLast(new PortDataClientHandler());
                    }
                }).connect(address).addListener(future -> {
                            log.info("port data client[{}] of session[{}] is connected", address, session);
                        }
                );
        return this.channelFuture;
    }

    /**
     * 传输数据方法, 需要传入实际函数实现
     *
     * @param function 函数入参为 channel, 返回当前连接通道供调用者传输. 返回值为 ChannelFuture, 可通过此值监听数据传输状态
     * @return
     */
    public Promise<Void> writeAndFlushData(Function<Channel, ChannelFuture> function) {
        Promise<Void> promise = session.getCtx().channel().eventLoop().newPromise();
        // 调用函数并传入当前连接的 channel 通道, 并追加监听, 当函数执行完毕后通知监听此 Promise 的方法被调用
        function.apply(this.channelFuture.channel()).addListener(future -> {
            promise.setSuccess(null);
        });
        return promise;
    }

    /**
     * 关闭连接, 返回 Promise 供需要监听此操作的功能使用
     *
     * @return
     */
    public Promise<Void> close() {
        Promise<Void> promise = session.getCtx().channel().eventLoop().newPromise();
        channelFuture.channel().close().addListener(future -> {
            log.info("port data client[{}] of session[{}] is closed", this, session);
            promise.setSuccess(null);
        });
        return promise;
    }

    class PortDataClientHandler extends SimpleChannelInboundHandler {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            FileView file = session.getFileSystemView().getFile(session.getUploadFilePath());
            try (
                    final RandomAccessFile randomAccessFile = new RandomAccessFile(file.getFile(), "rw")
            ) {
                ByteBuf buffer = (ByteBuf) msg;
                int length;
                while ((length = buffer.readableBytes()) > 0) {
                    buffer.readBytes(randomAccessFile.getChannel(), length);
                }
            }

        }
    }
}
