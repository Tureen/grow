package club.tulane.nio.advanced.ftp.codec;

import club.tulane.nio.advanced.ftp.model.FtpResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang.StringUtils;

public class FtpEncoder extends MessageToByteEncoder<FtpResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, FtpResponse response, ByteBuf out) {
        String line = String.valueOf(response.getCode());
        if (StringUtils.isNotBlank(response.getMessage())) {
            line += " " + response.getMessage();
        }
        line += "\r\n";
        out.writeBytes(line.getBytes(CharsetUtil.UTF_8));
    }
}
