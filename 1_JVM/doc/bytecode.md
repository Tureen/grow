# 字节码程序翻译

操作步骤
1. 编辑源程序, 位置为 `/Users/Tulane/项目/grow/1_JVM/src/main/java/club/tulane/jvm/bytecode/HelloCode.java`
2. 编译程序生成 class 文件, 位置为 `/Users/Tulane/项目/grow/1_JVM/target/classes/club/tulane/jvm/bytecode/HelloCode.class`
3. 打开 iterm, 通过 cd 命令进入 class 文件夹, 位置为 `/Users/Tulane/项目/grow/1_JVM/target/classes/club/tulane/jvm/bytecode/HelloCode.class`
4. 输入查看字节码文件助记符的命令: `javap -c -verbose club.tulane.jvm.bytecode.HelloCode`


## 源程序
```java
package club.tulane.jvm;

public class HelloCode {

    public static void foo(){
        int a = 1;
        int b = 2;
        int c = (a + b) * 5;
    }
}

```

## 字节码-标注版
```shell
Classfile /Users/Tulane/项目/grow/1_JVM/target/classes/club/tulane/jvm/bytecode/HelloCode.class
  Last modified 2021-2-12; size 405 bytes
  MD5 checksum d8d813d23af6014af487291a2f7b4586
  Compiled from "HelloCode.java"
public class club.tulane.jvm.bytecode.HelloCode
  minor version: 0                          // 副版本
  major version: 52                         // 主版本: 52.0 表示jdk8
  flags: ACC_PUBLIC, ACC_SUPER              // 标识类是public
Constant pool:                              // 常量池
   #1 = Methodref          #3.#18         // java/lang/Object."<init>":()V
   #2 = Class              #19            // club/tulane/jvm/bytecode/HelloCode
   #3 = Class              #20            // java/lang/Object
   #4 = Utf8               <init>
   #5 = Utf8               ()V
   #6 = Utf8               Code
   #7 = Utf8               LineNumberTable
   #8 = Utf8               LocalVariableTable
   #9 = Utf8               this
  #10 = Utf8               Lclub/tulane/jvm/HelloCode;
  #11 = Utf8               foo
  #12 = Utf8               a
  #13 = Utf8               I
  #14 = Utf8               b
  #15 = Utf8               c
  #16 = Utf8               SourceFile
  #17 = Utf8               HelloCode.java
  #18 = NameAndType        #4:#5          // "<init>":()V
  #19 = Utf8               club/tulane/jvm/HelloCode
  #20 = Utf8               java/lang/Object
{
  public club.tulane.jvm.bytecode.HelloCode();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1    // 栈深 1  变量 1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 3: 0
      LocalVariableTable:               // 本地变量表
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lclub/tulane/jvm/HelloCode;

  public static void foo();
    descriptor: ()V
    flags: ACC_PUBLIC, ACC_STATIC       // 标识 foo 是静态 public 方法
    Code:
      stack=2, locals=3, args_size=0    // 栈深 2, 变量 3
         0: iconst_1                    // 定义常量 1
         1: istore_0                    // 将栈上常量放入本地变量表 0 槽位
         2: iconst_2                    // 定义常量 2
         3: istore_1                    // 将栈上常量放入本地变量表 1 槽位
         4: iload_0                     // 从本地变量表中将槽位 0 的数据取出放到栈
         5: iload_1                     // 从本地变量表中将槽位 1 的数据取出放到栈
         6: iadd                        // 栈上数据进行 "相加操作"
         7: iconst_5                    // 定义常量 5 (此时常量在栈上)
         8: imul                        // 栈上数据进行 "相乘操作"
         9: istore_2                    // 将栈上常量放入本地变量表 2 槽位
        10: return
      LineNumberTable:                  // 源码行号 与 字节码行号 对应表
        line 6: 0
        line 7: 2
        line 8: 4
        line 9: 10
      LocalVariableTable:               // 本地变量表
        Start  Length  Slot  Name   Signature
            2       9     0     a   I
            4       7     1     b   I
           10       1     2     c   I
}
SourceFile: "HelloCode.java"
```
