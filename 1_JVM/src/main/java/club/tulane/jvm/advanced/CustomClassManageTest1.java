package club.tulane.jvm.advanced;

import java.lang.reflect.Method;

/**
 * 此类对应进阶作业1: 使用自定义Classloader机制，实现xlass的加载：xlass是作业材料。
 */
public class CustomClassManageTest1 {

    public static void main(String[] args) {
        try {
            // 构建类名路径映射关系
            new CustomClassManage("1_JVM/lib/Hello.xlass").loadFromLib();

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
