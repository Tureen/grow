package club.tulane.jvm.classunloade;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 加载与卸载类测试
 *
 * 启动类参数加上: -XX:+TraceClassLoading -XX:+TraceClassUnloading 打印加载与卸载
 */
public class ClassUnloadTest {

    public static void main(String[] args) throws InterruptedException {

        // 等待两秒，让系统加载完所有的类
        Thread.sleep(2000);

        try {
            System.out.println("Loading...");

            // 利用反射，加载ComplexClass类
            URLClassLoader loader = new URLClassLoader(new URL[]{new URL("file:1_JVM/lib/hello.jar")});
            loader.loadClass("Hello");

            // 将加载这个类的URLClassLoader的引用置为null，以便让这个类释放
            loader = null;

            // 启动垃圾回收（JVM虚拟机规范中明确说明，这个方法并不能保证垃圾回收一定执行，但是在此处的确有执行）
            System.gc();

        } catch (MalformedURLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
