package club.tulane.nio.advanced.ftp.command.impl;

import club.tulane.nio.advanced.ftp.FtpReply;
import club.tulane.nio.advanced.ftp.model.FileSystemView;
import club.tulane.nio.advanced.ftp.model.FtpRequest;
import club.tulane.nio.advanced.ftp.model.FtpResponse;
import club.tulane.nio.advanced.ftp.model.FtpSession;
import club.tulane.nio.advanced.ftp.command.Command;

public class USER implements Command {

    @Override
    public FtpResponse execute(FtpRequest request, FtpSession session) {
        session.setLoggedIn(true);
        session.setFileSystemView(new FileSystemView());
        return new FtpResponse(FtpReply.REPLY_230);
    }
}
