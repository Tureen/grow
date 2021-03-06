package club.tulane.nio.advanced.ftp.model;

import club.tulane.nio.advanced.ftp.command.Command;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
public class FtpRequest {

    private Command command;

    private String argument;

    private FtpSession ftpSession;

    private Map<String, Object> params = new HashMap<>();

    public FtpRequest(Command command, String argument) {
        this.command = command;
        this.argument = argument;
    }

    public FtpRequest addParam(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public <T> T getParam(String key) {
        return (T) params.get(key);
    }

    public boolean hasArgument() {
        return StringUtils.isNotBlank(argument);
    }
}
