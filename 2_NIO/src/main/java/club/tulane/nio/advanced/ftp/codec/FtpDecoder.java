package club.tulane.nio.advanced.ftp.codec;

import club.tulane.nio.advanced.ftp.model.FtpRequest;
import club.tulane.nio.advanced.ftp.command.Command;
import club.tulane.nio.advanced.ftp.command.CommandFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.util.CharsetUtil;

/**
 * ftp协议解码器
 *
 * 继承 LineBasedFrameDecoder 分隔类, 当识别 \r\n 时做包分隔
 * 作用是分隔包以及将数据转换为 FtpRequest 内部使用的请求对象
 */
public class FtpDecoder extends LineBasedFrameDecoder {

    public FtpDecoder() {
        super(4096);
    }

    @Override
    protected FtpRequest decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        final ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if(frame == null){
            return null;
        }

        String line = frame.toString(CharsetUtil.UTF_8);
        String[] arr = line.split(" ", 2);

        final String commandStr = arr[0].trim();
        final Command command = CommandFactory.getCommand(commandStr);

        return new FtpRequest(command, arr.length > 1 ? arr[1].trim() : null);
    }
}
