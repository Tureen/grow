package club.tulane.nio.advanced.ftp.command.impl;

import club.tulane.nio.advanced.ftp.FtpReply;
import club.tulane.nio.advanced.ftp.command.Command;
import club.tulane.nio.advanced.ftp.model.FileView;
import club.tulane.nio.advanced.ftp.model.FtpRequest;
import club.tulane.nio.advanced.ftp.model.FtpResponse;
import club.tulane.nio.advanced.ftp.model.FtpSession;

/**
 * ftp命令: pwd
 */
public class PWD implements Command {

    @Override
    public FtpResponse execute(FtpRequest request, FtpSession session) {
        final FileView fileView = session.getFileSystemView().getCurrentDirectory();
        return new FtpResponse(FtpReply.REPLY_257.getCode(), fileView.getVirtualPath());
    }
}
