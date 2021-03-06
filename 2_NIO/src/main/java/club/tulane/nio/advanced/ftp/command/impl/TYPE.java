package club.tulane.nio.advanced.ftp.command.impl;

import club.tulane.nio.advanced.ftp.FtpReply;
import club.tulane.nio.advanced.ftp.command.Command;
import club.tulane.nio.advanced.ftp.model.FtpRequest;
import club.tulane.nio.advanced.ftp.model.FtpResponse;
import club.tulane.nio.advanced.ftp.model.FtpSession;

/**
 * 命令: binary (接收二进制)
 * 接收流方式切换, 无需具体事先, 由ftp请求方自行切换
 */
public class TYPE implements Command {

	@Override
	public FtpResponse execute(FtpRequest request, FtpSession session) {
		return new FtpResponse(FtpReply.REPLY_200);
	}
}
