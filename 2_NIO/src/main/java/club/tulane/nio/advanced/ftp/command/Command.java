package club.tulane.nio.advanced.ftp.command;

import club.tulane.nio.advanced.ftp.model.FtpRequest;
import club.tulane.nio.advanced.ftp.model.FtpResponse;
import club.tulane.nio.advanced.ftp.model.FtpSession;

public interface Command {

    String[] NON_AUTHENTICATED_COMMANDS = {"USER", "PASS", "QUIT"};

    FtpResponse execute(FtpRequest request, FtpSession session);
}
