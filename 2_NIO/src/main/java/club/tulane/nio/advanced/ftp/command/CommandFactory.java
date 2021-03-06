package club.tulane.nio.advanced.ftp.command;

import club.tulane.nio.advanced.ftp.command.impl.*;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * 指令工厂
 */
public class CommandFactory {

	private static Map<String, Command> COMMANDS = ImmutableMap.<String, Command>builder()
			.put("USER", new USER())
			.put("PWD", new PWD())
			.put("CWD", new CWD())
			.put("PORT", new PORT())
			.put("LIST", new LIST())
			.put("RETR", new RETR())
			.put("TYPE", new TYPE())
			.put("STOR", new STOR())
			.build();

	public static Command getCommand(String name) {
		return COMMANDS.get(name.toUpperCase());
	}
}
