package club.tulane.jvm.advanced;

import java.lang.reflect.Method;

/**
 * 此类对应进阶作业2: 实现xlass打包的xar（类似class文件打包的jar）的加载：xar里是xlass。
 */
public class WorkTest2 {

    public static void main(String[] args) {
        try {
            // 构建类名路径映射关系: 将xar包下资源读取到临时目录, 并建立对应映射关系的缓存
            CustomClassLoader customClassLoader = new CustomClassLoader();
            customClassLoader.addURL("1_JVM/lib/hello.xar");

            // 测试加载类
            final Class<?> hello = new CustomClassLoader().loadClass("Hello");
            final Object helloObj = hello.newInstance();
            final Method helloMethod = hello.getMethod("hello");
            helloMethod.invoke(helloObj);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
