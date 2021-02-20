package club.tulane.jvm.advanced;

import java.io.File;
import java.util.Map;

public class CustomClassManage {

    private String lib;

    public CustomClassManage(String lib) {
        this.lib = lib;
    }

    /**
     * 利用 CustomClassLoader 加载指定路径的类
     * @throws Exception
     */
    public CustomClassLoader loadFromLib() throws Exception {
        CustomClassLoader customClassLoader = new CustomClassLoader();
        customClassLoader.addURL(lib);

        for (Map.Entry<String, File> entry : CustomClassLoader.FILEMAP.entrySet()) {
            customClassLoader.loadClass(entry.getKey());
        }
        return customClassLoader;
    }
}
