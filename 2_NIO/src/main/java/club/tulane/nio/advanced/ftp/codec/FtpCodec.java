package club.tulane.nio.advanced.ftp.codec;

import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * ftp编解码对象
 *
 * 利用 Netty 的 CombinedChannelDuplexHandler 包装自定义的编解码对象, 供数据在 Netty 管道中的接收与传输
 */
public class FtpCodec extends CombinedChannelDuplexHandler<FtpDecoder, FtpEncoder> {

    public FtpCodec() {
        super(new FtpDecoder(), new FtpEncoder());
    }
}
