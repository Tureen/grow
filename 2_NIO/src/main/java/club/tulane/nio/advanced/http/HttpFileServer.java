package club.tulane.nio.advanced.http;

import club.tulane.nio.advanced.common.ImproperOptionException;
import club.tulane.nio.advanced.common.SystemException;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpFileServer{

    private final ResourceFile resourceFile;
    private final HttpFileSearch httpFileSearchServer;
    private final HttpFileSend httpFileSendServer;

    @Getter
    private ThreadStatusEnum threadStatusEnum;

    public HttpFileServer(String url) {
        this.resourceFile = new ResourceFile();
        this.httpFileSearchServer = new HttpFileSearch(url, resourceFile);
        this.httpFileSendServer = new HttpFileSend();
        this.threadStatusEnum = ThreadStatusEnum.RUN;
    }

    public void serverFile(ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {
        try {
            serverFileWithException(ctx, request);
        } catch (SystemException e) {
            threadStatusEnum = ThreadStatusEnum.INTERRUPT;
            sendError(ctx, e.getStatus());
        }
    }

    private void serverFileWithException(ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {
        // 检索文件
        File file = httpFileSearchServer.searchFile(ctx, request);

        if (checkThreadStatus(ctx, request, file)) return;

        // 构建随机读写文件类
        RandomAccessFile randomAccessFile = buildLoadFile(ctx, file);

        // 发送文件信息
        httpFileSendServer.sendFile(ctx, request, file.getPath(), randomAccessFile);
    }

    private boolean checkThreadStatus(ChannelHandlerContext ctx, FullHttpRequest request, File file) {
        final ThreadStatusEnum threadStatusEnumForSearch = httpFileSearchServer.getThreadStatusEnum();
        if(threadStatusEnumForSearch == ThreadStatusEnum.LOAD_DIR){
            threadStatusEnum = threadStatusEnumForSearch;
            httpFileSendServer.sendDirs(ctx, resourceFile.showDir(file).toString());
            return true;
        }

        if(threadStatusEnumForSearch == ThreadStatusEnum.REDIRECT){
            threadStatusEnum = threadStatusEnumForSearch;
            sendRedirect(ctx, request.uri() + '/');
            return true;
        }

        if(threadStatusEnumForSearch == ThreadStatusEnum.INTERRUPT){
            threadStatusEnum = threadStatusEnumForSearch;
            return true;
        }
        return false;
    }

    private RandomAccessFile buildLoadFile(ChannelHandlerContext ctx, File file) {
        // 随机读写File类, 下载时断点续传
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            throw new ImproperOptionException(NOT_FOUND);
        }
        return randomAccessFile;
    }

    protected void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                status, Unpooled.copiedBuffer("Failure: " + status.toString()
                + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 重定向
     * @param ctx
     * @param newUri
     */
    protected void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(HttpHeaderNames.LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
