package club.tulane.nio.advanced.ftp.codec;

import io.netty.channel.CombinedChannelDuplexHandler;

public class FtpCodec extends CombinedChannelDuplexHandler<FtpDecoder, FtpEncoder> {

    public FtpCodec() {
        super(new FtpDecoder(), new FtpEncoder());
    }
}
