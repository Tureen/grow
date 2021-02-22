package club.tulane.nio.advanced.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpFileServer extends AbstractHttpFileServer {

    private final String url;
    private final ResourceFile resourceFile;

    public HttpFileServer(String url) {
        this.url = url;
        this.resourceFile = new ResourceFile();
    }

    @Override
    protected StringBuilder searchDir(File dir) {
        return resourceFile.showDir(dir);
    }

    public boolean serverFile(ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {
        // 检索文件
        File file = searchFile(ctx, request);
        if (file == null) return true;

        // 构建随机读写文件类
        RandomAccessFile randomAccessFile = buildLoadFile(ctx, file);
        if (randomAccessFile == null) return true;

        // 发送文件信息
        sendFile(ctx, request, file.getPath(), randomAccessFile);
        return false;
    }

    private File searchFile(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (validateRequest(ctx, request)) return null;

        final String uri = request.uri();
        // 对请求uri包装
        final String path = resourceFile.sanitizeUri(uri, url);
        if(path == null){
            // 消毒后路径为空, 返回路径资源不可用
            sendError(ctx, FORBIDDEN);
            return null;
        }
        File file = new File(path);
        if(file.isHidden() || !file.exists()){
            // 如果文件隐藏或者不存在, 返回文件不存在
            sendError(ctx, NOT_FOUND);
            return null;
        }

        // 如果文件输入文件夹, 进入文件夹流程, 并 return
        if(file.isDirectory()){
            if(uri.endsWith("/")){
                // 目录 html
                sendDirs(ctx, file);
            }else{
                // 302重定向
                sendRedirect(ctx, uri + '/');
            }
            return null;
        }

        if(!file.isFile()){
            // 如果非文件, 返回资源不可用错误
            sendError(ctx, FORBIDDEN);
            return null;
        }
        return file;
    }

    private RandomAccessFile buildLoadFile(ChannelHandlerContext ctx, File file) {
        // 随机读写File类, 下载时断点续传
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            sendError(ctx, NOT_FOUND);
            return null;
        }
        return randomAccessFile;
    }

    private boolean validateRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        if(!request.decoderResult().isSuccess()){
            // 连接请求解码失败处理
            sendError(ctx, BAD_REQUEST);
            return true;
        }
        if(request.method() != HttpMethod.GET){
            // 请求不是 GET 类型处理
            sendError(ctx, METHOD_NOT_ALLOWED);
            return true;
        }
        return false;
    }

    private void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(HttpHeaderNames.LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                status, Unpooled.copiedBuffer("Failure: " + status.toString()
                + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
