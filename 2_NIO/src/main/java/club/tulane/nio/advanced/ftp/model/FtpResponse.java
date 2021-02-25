package club.tulane.nio.advanced.ftp.model;

import club.tulane.nio.advanced.ftp.FtpReply;
import club.tulane.nio.advanced.ftp.command.Command;
import io.netty.channel.ChannelPromise;
import lombok.Data;

import java.util.function.Supplier;

@Data
public class FtpResponse {

    private Command command;

    private int code;

    private String message;

    private Supplier supplier;

    private ChannelPromise channelPromise;

    public FtpResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public FtpResponse(int code) {
        this.code = code;
    }

    public FtpResponse(FtpReply ftpReply) {
        this.code = ftpReply.getCode();
        this.message = ftpReply.getDescription();
    }
}
