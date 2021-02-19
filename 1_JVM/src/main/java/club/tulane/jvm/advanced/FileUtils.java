package club.tulane.jvm.advanced;

import java.io.File;

public class FileUtils {

    /**
     * 获取文件后缀
     * @param file
     * @return
     */
    public static String getFileExtension(File file) {
        String extension = "";
        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        } catch (Exception e) {
            extension = "";
        }
        return extension;
    }

    /**
     * 获取文件名称 (没有后缀)
     * @param file
     * @return
     */
    public static String getFileName(File file){
        String filename = "";
        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                filename = name.substring(0, name.lastIndexOf("."));
            }
        } catch (Exception e) {
            filename = "";
        }
        return filename;
    }
}
