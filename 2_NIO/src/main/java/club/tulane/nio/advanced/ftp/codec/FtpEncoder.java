package club.tulane.nio.advanced.ftp.codec;

import club.tulane.nio.advanced.ftp.model.FtpResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang.StringUtils;

/**
 * ftp协议编码器
 *
 * 作用是将自定义响应对象转化为流数据, 供传输给ftp请求方
 */
public class FtpEncoder extends MessageToByteEncoder<FtpResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, FtpResponse response, ByteBuf out) {
        String line = String.valueOf(response.getCode());
        if (StringUtils.isNotBlank(response.getMessage())) {
            line += " " + response.getMessage();
        }
        // 末尾加上回车标识, 供ftp请求方分包
        line += "\r\n";
        out.writeBytes(line.getBytes(CharsetUtil.UTF_8));
    }
}
