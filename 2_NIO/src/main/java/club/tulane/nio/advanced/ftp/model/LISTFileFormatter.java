package club.tulane.nio.advanced.ftp.model;

public class LISTFileFormatter {

    private final static char DELIM = ' ';

    private final static char[] NEWLINE = { '\r', '\n' };

	public String format(FileView file) {
        StringBuilder sb = new StringBuilder();
		        sb.append(file.getName());
		        sb.append(NEWLINE);
        return sb.toString();
	}

}
