package club.tulane.nio.advanced.ftp.command.impl;

import club.tulane.nio.advanced.ftp.FtpReply;
import club.tulane.nio.advanced.ftp.command.Command;
import club.tulane.nio.advanced.ftp.model.FileSystemView;
import club.tulane.nio.advanced.ftp.model.FtpRequest;
import club.tulane.nio.advanced.ftp.model.FtpResponse;
import club.tulane.nio.advanced.ftp.model.FtpSession;
import org.apache.commons.lang.StringUtils;

/**
 * 命令: cd
 */
public class CWD implements Command {

    @Override
    public FtpResponse execute(FtpRequest request, FtpSession session) {
        // 构造切换的文件路径
        final String path = StringUtils.defaultString(request.getArgument(), "/");

        // 执行切换用户文件路径动作
        final FileSystemView userFileSystemView = session.getFileSystemView();
        final boolean ischange = userFileSystemView.changeCurrentDirectory(path);

        if(ischange){
            return new FtpResponse(FtpReply.REPLY_250.getCode(), userFileSystemView.getCurrentDirectory().getVirtualPath());
        }
        return new FtpResponse(FtpReply.REPLY_550);
    }
}
