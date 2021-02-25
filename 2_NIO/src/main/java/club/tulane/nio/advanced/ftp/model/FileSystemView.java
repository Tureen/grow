package club.tulane.nio.advanced.ftp.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

@Slf4j
@Data
public class FileSystemView {

    private FileView homeDirectory;

    private FileView currentDirectory;

    public FileSystemView() {
        this.homeDirectory = new FileView("/");
        this.currentDirectory = this.homeDirectory;
    }

    public boolean changeCurrentDirectory(String dir) {
        try {
            FileView newDirectory = new FileView(FilenameUtils.concat(currentDirectory.getVirtualPath(), dir));
            if (newDirectory.isDirectory()) {
                this.currentDirectory = newDirectory;
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("failed to change current directory from {} to {}", currentDirectory.getVirtualPath(), dir, e);
            return false;
        }
    }

    public FileView getFile(String fileName) {
        return new FileView(FilenameUtils.concat(getCurrentDirectory().getVirtualPath(), fileName));
    }
}
