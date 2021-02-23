package club.tulane.nio.advanced.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.IOException;

public class Server {

    private static final String DEFAULT_URL = "/src/";

    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        String url = DEFAULT_URL;
        if (args.length > 1)
            url = args[1];
        new Server().run(port, url);
    }

    public void run(final int port, final String url) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 请求解密
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            // 多个消息转换为单个 FullHttpReuqest(FullHttpResponse) , 因为 Http解码器每个Http消息会生成多个消息对象
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            // 支持异步发送大码流(例如文件) 不占用过多内存防止内存溢出
                            ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            ch.pipeline().addLast("fileServerHandler", new SimpleChannelInboundHandler<FullHttpRequest>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {

                                    HttpFileServer httpFileServer = new HttpFileServer(url);

                                    // 查找并发送文件
                                    httpFileServer.serverFile(ctx, request);
                                    if(httpFileServer.getThreadStatusEnum() != ThreadStatusEnum.RUN){
                                        return;
                                    }

                                    // 传输完成, 传入空作为结束标志
                                    final ChannelFuture channelFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                                    if(!HttpUtil.isKeepAlive(request)){
                                        channelFuture.addListener(ChannelFutureListener.CLOSE);
                                    }
                                }
                            });
                        }
                    });
            final ChannelFuture f = b.bind("127.0.0.1", port).sync();
            System.out.println("HTTP文件目录服务器启动，网址是 : " + "http://127.0.0.1:" + port + url);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }
}
