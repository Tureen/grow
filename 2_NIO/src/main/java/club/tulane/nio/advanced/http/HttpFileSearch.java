package club.tulane.nio.advanced.http;

import club.tulane.nio.advanced.common.ImproperOptionException;
import club.tulane.nio.advanced.common.NormalOptionException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;

import java.io.File;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

public class HttpFileSearch {

    private final String url;
    private final ResourceFile resourceFile;

    @Getter
    private ThreadStatusEnum threadStatusEnum;

    public HttpFileSearch(String url, ResourceFile resourceFile) {
        this.url = url;
        this.resourceFile = resourceFile;
        this.threadStatusEnum = ThreadStatusEnum.RUN;
    }

    /**
     * 搜索文件
     * @param ctx
     * @param request
     * @return
     */
    public File searchFile(ChannelHandlerContext ctx, FullHttpRequest request) {
        validateRequest(request);

        final String uri = request.uri();
        // 对请求uri包装
        final String path = resourceFile.sanitizeUri(uri, url);
        if(path == null){
            // 消毒后路径为空, 返回路径资源不可用
            throw new NormalOptionException(FORBIDDEN);
        }
        File file = new File(path);
        if(file.isHidden() || !file.exists()){
            // 如果文件隐藏或者不存在, 返回文件不存在
            throw new NormalOptionException(NOT_FOUND);
        }

        // 如果文件输入文件夹, 进入文件夹流程, 并 return
        if(file.isDirectory()){
            if(uri.endsWith("/")){
                // 目录 html
                threadStatusEnum = ThreadStatusEnum.LOAD_DIR;
                return file;
            }else{
                // 302重定向
                threadStatusEnum = ThreadStatusEnum.REDIRECT;
                return null;
            }
        }

        if(!file.isFile()){
            // 如果非文件, 返回资源不可用错误
            throw new NormalOptionException(FORBIDDEN);
        }
        return file;
    }

    private void validateRequest(FullHttpRequest request) {
        if(!request.decoderResult().isSuccess()){
            // 连接请求解码失败处理
            throw new ImproperOptionException(BAD_REQUEST);
        }
        if(request.method() != HttpMethod.GET){
            // 请求不是 GET 类型处理
            throw new ImproperOptionException(METHOD_NOT_ALLOWED);
        }
    }
}
