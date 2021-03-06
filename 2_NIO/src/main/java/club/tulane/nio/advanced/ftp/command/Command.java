package club.tulane.nio.advanced.ftp.command;

import club.tulane.nio.advanced.ftp.model.FtpRequest;
import club.tulane.nio.advanced.ftp.model.FtpResponse;
import club.tulane.nio.advanced.ftp.model.FtpSession;

/**
 * 命令接口
 */
public interface Command {

    // 不校验权限的命令 (暂未使用)
    String[] NON_AUTHENTICATED_COMMANDS = {"USER", "QUIT"};

    /**
     * 执行指令方法
     * @param request 请求对象
     * @param session 会话
     * @return
     */
    FtpResponse execute(FtpRequest request, FtpSession session);
}
