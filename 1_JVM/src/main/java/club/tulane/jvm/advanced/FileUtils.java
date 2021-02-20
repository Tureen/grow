package club.tulane.jvm.advanced;

import java.io.File;

public class FileUtils {

    /**
     * 检查文件后缀是否符合
     *
     * @param file
     * @return
     */
    public static boolean checkFileExtension(File file, String fileType) {
        final String fileExtension = FileUtils.getFileExtension(file);
        if (fileType.equals(fileExtension)) {
            return true;
        }
        return false;
    }

    /**
     * 检查文件名后缀是否符合
     *
     * @param name
     * @return
     */
    public static boolean checkExtension(String name, String fileType) {
        final String extension = FileUtils.getExtension(name);
        if (fileType.equals(extension)) {
            return true;
        }
        return false;
    }

    /**
     * 获取文件后缀
     * @param file
     * @return
     */
    public static String getFileExtension(File file) {
        String extension = "";
        if (file != null && file.exists()) {
            String name = file.getName();
            extension = getExtension(name);
        }
        return extension;
    }

    public static String getExtension(String name) {
        String extension = "";
        try {
            extension = name.substring(name.lastIndexOf("."));
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
