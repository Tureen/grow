package club.tulane.nio.advanced.ftp;

import club.tulane.nio.advanced.ftp.codec.FtpCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {

    private static final String DEFAULT_URL = "/src/";

    public static void main(String[] args) {
        int port = 8080;
        String url = DEFAULT_URL;
        new Server().run(port, url);
    }

    public void run(final int port, final String url) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 请求解密
                            ch.pipeline()
                                    .addLast("ftp-decoder", new FtpCodec())
                                    .addLast(new IdleStateHandler(0, 0, 120))
                                    .addLast(new FtpHandler());
                        }
                    });
            final ChannelFuture f = b.bind("127.0.0.1", port).sync().addListener(f2 -> {
                log.info("ftp server is started");
            });
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }
}
