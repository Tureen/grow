package club.tulane.nio.advanced.ftp.command;

import club.tulane.nio.advanced.ftp.command.impl.*;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public interface CommandFactory {

	Map<String, Command> COMMANDS = ImmutableMap.<String, Command>builder()
			.put("USER", new USER())
			.put("PWD", new PWD())
			.put("CWD", new CWD())
			.put("PORT", new PORT())
			.put("LIST", new LIST())
			.build();

	static Command getCommand(String name) {
		return COMMANDS.get(name.toUpperCase());
	}
}
