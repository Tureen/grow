package club.tulane.nio.advanced.ftp.model;

import lombok.Data;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class FileView {

    public static final String HOME_DIRECTORY = "2_NIO/src";
    private File file;

    private String virtualPath;

    private String realPath;

    public FileView(String path) {
        if (StringUtils.isBlank(path)) {
            path = "/";
        }
        this.virtualPath = "/".equals(path) ? "/" : StringUtils.stripEnd(path, "/");
        this.file = new File(HOME_DIRECTORY, StringUtils.stripStart(this.virtualPath, "/"));
        this.realPath = file.getAbsolutePath();
    }

    public boolean doesExist() {
        return this.file.exists();
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public boolean isFile() {
        return file.isFile();
    }

    public long getSize() {
        return file.length();
    }

    public long getLastModified() {
        return file.lastModified();
    }

    public String getName() {

        // root - the short name will be '/'
        if (virtualPath.equals("/")) {
            return "/";
        }

        // strip the last '/'
        String shortName = virtualPath;
        int filelen = virtualPath.length();
        if (shortName.charAt(filelen - 1) == '/') {
            shortName = shortName.substring(0, filelen - 1);
        }

        // return from the last '/'
        int slashIndex = shortName.lastIndexOf('/');
        if (slashIndex != -1) {
            shortName = shortName.substring(slashIndex + 1);
        }
        return shortName;
    }

    public List<FileView> listFiles() {
        if (!file.isDirectory()) {
            return Collections.emptyList();
        }
        File[] files = file.listFiles();
        if (ArrayUtils.isEmpty(files)) {
            return Collections.emptyList();
        }
        return Arrays.stream(files)
                .map(file -> file.getName())
                .map(name -> new FileView(FilenameUtils.concat(virtualPath, name)))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object other) {
        if (!FileView.class.isInstance(other)) {
            return false;
        }
        return StringUtils.equals(realPath, FileView.class.cast(other).realPath);
    }
}
