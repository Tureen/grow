package club.tulane.nio.advanced.ftp.command.impl;

import club.tulane.nio.advanced.ftp.FtpReply;
import club.tulane.nio.advanced.ftp.command.Command;
import club.tulane.nio.advanced.ftp.model.FtpRequest;
import club.tulane.nio.advanced.ftp.model.FtpResponse;
import club.tulane.nio.advanced.ftp.model.FtpSession;
import org.apache.commons.lang.StringUtils;

/**
 * ftp命令: cd
 */
public class CWD implements Command {

    @Override
    public FtpResponse execute(FtpRequest request, FtpSession session) {
        final String path = StringUtils.defaultString(request.getArgument(), "/");
        final boolean ischange = session.getFileSystemView().changeCurrentDirectory(path);
        if(ischange){
            return new FtpResponse(FtpReply.REPLY_250.getCode(), session.getFileSystemView().getCurrentDirectory().getVirtualPath());
        }
        return new FtpResponse(FtpReply.REPLY_550);
    }
}
