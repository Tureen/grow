package club.tulane.jvm.uninit.constant;

/**
 * 类初始化测试: 常量调用
 *
 * 调用常量时不会触发其所在类的初始化, 因为编译期该常量就存入调用类的常量池, 与所在类无关
 */
public class ConstantTest {

    public static void main(String[] args) {
        String name = Constant.NAME;
    }
}
