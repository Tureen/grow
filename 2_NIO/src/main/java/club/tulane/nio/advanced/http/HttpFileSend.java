package club.tulane.nio.advanced.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import lombok.Getter;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpFileSend {

    @Getter
    protected ThreadStatusEnum threadStatusEnum;

    public HttpFileSend() {
        this.threadStatusEnum = ThreadStatusEnum.RUN;
    }

    /**
     * 发送菜单信息
     * @param ctx
     * @param text
     */
    public void sendDirs(ChannelHandlerContext ctx, String text) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

        final ByteBuf buffer = Unpooled.copiedBuffer(text, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 发送文件
     * @param ctx
     * @param request
     * @param filePath
     * @param randomAccessFile
     * @throws IOException
     */
    public void sendFile(ChannelHandlerContext ctx, FullHttpRequest request, String filePath,
                         RandomAccessFile randomAccessFile) throws IOException {

        sendFileHead(ctx, request, filePath, randomAccessFile);

        ChannelFuture sendFileFuture = ctx.write(
                new ChunkedFile(randomAccessFile, 0, randomAccessFile.length(), 8192), ctx.newProgressivePromise());
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            // 进度监听
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                if (total < 0) {
                    System.err.println("Transfer progress: " + progress);
                } else {
                    System.err.println("Transfer progress: " + progress + " / " + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) {
                System.out.println("Transfer complete.");
            }
        });
    }

    /**
     * 发送文件的响应头
     * @param ctx
     * @param request
     * @param filePath
     * @param randomAccessFile
     * @throws IOException
     */
    private void sendFileHead(ChannelHandlerContext ctx, FullHttpRequest request, String filePath, RandomAccessFile randomAccessFile) throws IOException {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpUtil.setContentLength(response, randomAccessFile.length());
        setContentTypeHeader(response, filePath);
        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.write(response);
    }

    /**
     * 设置响应头的 ContentType
     * @param response
     * @param filePath
     */
    private void setContentTypeHeader(HttpResponse response, String filePath) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(CONTENT_TYPE,
                mimeTypesMap.getContentType(filePath));
    }
}
