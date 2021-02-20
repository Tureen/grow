package club.tulane.jvm.advanced;

/**
 * 此类对应进阶作业3: 基于自定义Classloader实现类的动态加载和卸载：需要设计加载和卸载。
 *
 * 使用时启动参数添加: -XX:+TraceClassUnloading 观察类卸载情况
 */
public class WorkTest3 {

    public static void main(String[] args) throws InterruptedException {
        // 等待两秒，让系统加载完所有的类
        Thread.sleep(2000);
        try {
            System.out.println("Loading...");

            // 加载 xlass
            CustomClassLoader customClassLoader = new CustomClassLoader();
            customClassLoader.addURL("1_JVM/lib/Hello.xlass");
            customClassLoader.loadClass("Hello");
            customClassLoader = null;

            // 重新加载 xlass: 此时 customClassLoader 所加载的类会被动卸载
            CustomClassLoader customClassLoader2 = new CustomClassLoader();
            customClassLoader2.addURL("1_JVM/lib/Hello.xlass");
            customClassLoader2.loadClass("Hello");

            // 主动卸载 customClassLoader2 的加载类
            customClassLoader2 = null;
            CustomClassLoader.unLoadClassLoader("Hello");

            System.gc();

            Thread.sleep(20000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
