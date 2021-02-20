package club.tulane.jvm.classloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 自由添加引用类的方式
 *
 * 利用当前执行类的父类 URLClassLoader 的 addUrl 方法, 添加路径
 */
public class JvmAppClassLoaderAddURL {

    public static void main(String[] args) {
        String appPath = "file:1_JVM/lib/hello.jar";
        URLClassLoader urlClassLoader = (URLClassLoader) JvmAppClassLoaderAddURL.class.getClassLoader();
        try {
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
            URL url = new URL(appPath);
            addURL.invoke(urlClassLoader, url);
            Class.forName("Hello");
        } catch (NoSuchMethodException | MalformedURLException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
