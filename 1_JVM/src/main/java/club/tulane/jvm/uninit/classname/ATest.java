package club.tulane.jvm.uninit.classname;

/**
 * 类初始化测试: 类名.class
 *
 * 通过xx.class 不会触发类的初始化
 */
public class ATest {

    public static void main(String[] args) {
        final Class<A> aClass = A.class;
    }
}
