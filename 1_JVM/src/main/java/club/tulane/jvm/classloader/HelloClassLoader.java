package club.tulane.jvm.classloader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 自定义类加载器
 *
 * 重写 findClass() 方法, 直接读出 Hello.class 二进制并使用
 */
public class HelloClassLoader extends ClassLoader {

    public static void main(String[] args) {
        try {
            new HelloClassLoader().findClass("club.tulane.jvm.classloader.Hello").newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

//        System.out.println(new HelloClassLoader().encode());
    }

    @Override
    protected Class<?> findClass(String name) {
        String helloBase64 = "yv66vgAAADQAHwoABgARCQASABMIABQKABUAFgcAFwcAGAEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQAjTGNsdWIvdHVsYW5lL2p2bS9jbGFzc2xvYWRlci9IZWxsbzsBAAg8Y2xpbml0PgEAClNvdXJjZUZpbGUBAApIZWxsby5qYXZhDAAHAAgHABkMABoAGwEAF0hlbGxvIGNsYXNzIGluaXRpYWxpemVkBwAcDAAdAB4BACFjbHViL3R1bGFuZS9qdm0vY2xhc3Nsb2FkZXIvSGVsbG8BABBqYXZhL2xhbmcvT2JqZWN0AQAQamF2YS9sYW5nL1N5c3RlbQEAA291dAEAFUxqYXZhL2lvL1ByaW50U3RyZWFtOwEAE2phdmEvaW8vUHJpbnRTdHJlYW0BAAdwcmludGxuAQAVKExqYXZhL2xhbmcvU3RyaW5nOylWACEABQAGAAAAAAACAAEABwAIAAEACQAAAC8AAQABAAAABSq3AAGxAAAAAgAKAAAABgABAAAAAwALAAAADAABAAAABQAMAA0AAAAIAA4ACAABAAkAAAAlAAIAAAAAAAmyAAISA7YABLEAAAABAAoAAAAKAAIAAAAGAAgABwABAA8AAAACABA=";
        final byte[] bytes = decode(helloBase64);
        return defineClass(name, bytes, 0, bytes.length);
    }

    public byte[] decode(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    public String encode() {
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream("/Users/Tulane/项目/grow/1_JVM/target/classes/club/tulane/jvm/classloader/Hello.class"));
            byte[] bytes = new byte[buf.available()];
            buf.read(bytes);
            final byte[] encode = Base64.getEncoder().encode(bytes);
            return new String(encode, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
