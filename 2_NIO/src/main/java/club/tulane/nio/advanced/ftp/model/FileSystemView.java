package club.tulane.nio.advanced.ftp.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

/**
 * 系统文件目录视图
 */
@Slf4j
@Data
public class FileSystemView {

    /**
     * 主目录路径
     */
    private FileView homeDirectory;

    /**
     * 当前目录路径
     */
    private FileView currentDirectory;

    public FileSystemView() {
        this.homeDirectory = new FileView("/");
        this.currentDirectory = new FileView("/");
    }

    /**
     * 切换当前目录
     * @param dir
     * @return
     */
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
