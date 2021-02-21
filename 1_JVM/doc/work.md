# 作业

**常规** 

 - [X] 1（选做）自己写一个简单的 Hello.java，里面需要涉及基本类型，四则运行，if 和 for，然后自己分析一下对应的字节码，有问题群里讨论。
 - [X] 2（必做）自定义一个 Classloader，加载一个 Hello.xlass 文件，执行 hello 方法，此文件内容是一个 Hello.class 文件所有字节（x=255-x）处理后的文件。文件群里提供。
 - [X] 3（必做）画一张图，展示 Xmx、Xms、Xmn、Meta、DirectMemory、Xss 这些内存参数的关系。
 - [ ] 4（选做）检查一下自己维护的业务系统的 JVM 参数配置，用 jstat 和 jstack、jmap 查看一下详情，并且自己独立分析一下大概情况，思考有没有不合理的地方，如何改进。
 - [X] 5（选做）使用 GCLogAnalysis.java 自己演练一遍串行 / 并行 /CMS/G1 的案例。
 - [X] 6（选做）使用压测工具（wrk 或 sb），演练 gateway-server-0.0.1-SNAPSHOT.jar 示例。
 - [ ] 7（选做）如果自己本地有可以运行的项目，可以按照 6 的方式进行演练。
 - [X] 8（必做）根据上述自己对于 5 和 6 的演示，写一段对于不同 GC 的总结，提交到 Github。

**进阶**

从Classloader到模块化，动态加载的插件机制。

- [X] 1)10-使用自定义Classloader机制，实现xlass的加载：xlass是作业材料。
- [X] 2)20-实现xlass打包的xar（类似class文件打包的jar）的加载：xar里是xlass。
- [X] 3)30-基于自定义Classloader实现类的动态加载和卸载：需要设计加载和卸载。
- [ ] 4)30-基于自定义Classloader实现模块化机制：需要设计模块化机制。
- [ ] 5)30-使用xar作为模块，实现xar动态加载和卸载：综合应用前面的内容。

<br>

# 答题

常规1:[点击打开md文档](https://github.com/Tureen/grow/blob/main/1_JVM/doc/work/normal_1.md)

<br>

常规2:[点击打开md文档](https://github.com/Tureen/grow/blob/main/1_JVM/doc/work/normal_2.md)

<br>

常规3:[点击打开图片](https://github.com/Tureen/grow/blob/main/1_JVM/doc/images/JVM内存结构.jpg)

<br>

常规5:[点击打开md文档](https://github.com/Tureen/grow/blob/main/1_JVM/doc/work/normal_5.md)

<br>

常规6:[点击打开md文档](https://github.com/Tureen/grow/blob/main/1_JVM/doc/work/normal_6.md)

<br>

常规8:作业内容在 "常规5" 和 "常规6" 中

<br>

进阶1:[点击打开代码](https://github.com/Tureen/grow/tree/main/1_JVM/src/main/java/club/tulane/jvm/advanced/WorkTest1.java)

<br>

进阶2:[点击打开代码](https://github.com/Tureen/grow/tree/main/1_JVM/src/main/java/club/tulane/jvm/advanced/WorkTest2.java)

<br>

进阶3:[点击打开md文档](https://github.com/Tureen/grow/blob/main/1_JVM/doc/work/advanced_3.md)

<br>

关于自定义类加载器的设计思路: [点击打开md文档](https://github.com/Tureen/grow/blob/main/1_JVM/doc/work/design.md)