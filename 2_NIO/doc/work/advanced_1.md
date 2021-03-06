# 进阶作业1

<br>

## 简介

**题目**: 实现文件列表展示：http直接网页展示列表即可。ftp支持cd、ls命令。

**思路**: 需要做两个服务器, 并且都用 Netty 实现连接
1. 完成 HTTP 服务器的文件展示和目录跳转
2. 完成 FTP 服务器的文件展示和目录跳转

<br>

## HTTP 服务器介绍

代码目录在 [club.tulane.nio.advanced.http](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/http) 包下

http服务器启动代码: [点击打开代码](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/http/Server.java)

**编解码器**

借助 Netty 实现一个 HTTP 服务器并不难, 我们借助它自带的编解码器(HttpRequestDecoder、HttpResponseEncoder) 就能将流数据转换为内部使用的 FullHttpRequest 对象

**功能: 跳转与展示**

另外就是实现一个 InboundHandler 去处理 GET 请求下浏览器想访问的目录, 在代码中我建立 [HttpFileServer](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/http/HttpFileServer.java) 类做文件目录的搜索与展示

其中搜索功能对应的就是跳转各个目录, 展示功能就是将文件列表转给浏览器, 直接展示类似 TODO List 的文本信息

<br>

## FTP 服务器介绍

代码目录在 [club.tulane.nio.advanced.ftp](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp) 包下

ftp服务器启动代码: [点击打开代码](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp/Server.java)

**编解码器**

FTP 服务器的搭建则有些复杂, 由于没有内置的编解码器, 以及预置的传递请求的对象, 这些我们都要自己开发与定义

我定义的编解码器分别是 [FtpDecoder](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp/codec/FtpDecoder.java) 与 [FtpEncoder](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp/codec/FtpEncoder.java)
, 解码器利用 LineBasedFrameDecoder 这种识别回车符解码器分隔包, 并将 ByteBuf 转换为内部使用的  [FtpRequest](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp/model/FtpRequest.java)
, 编码器则会将内部使用的 [FtpResponse](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp/model/FtpResponse.java) 转换回 ByteBuf 并发送, 且会加上回车符帮助请求端做分包处理

**功能: 登录与会话**

由于 FTP 协议的指令非常多, 这里先写了一个策略模式, 抽象出 [Command](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp/command/Command.java) 命令接口

我们要实现各种功能前还需要实现登录, 所以这里实现 [USER](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp/command/impl/USER.java) 类做登录使用
, 并且实现会话机制 [FtpSession](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp/model/FtpSession.java) 用以区别各个连接者的资源情况, 比如当前访问的目录是什么. (为什么 HTTP 服务时没有做会话? 因为这个实现的 HTTP 服务是无状态的, 且没有保持连接每次请求都不带任何状态. 而 FTP 服务是需要保持连接的, 一次 FTP 连接就是一个会话, 它是有状态的)

**功能: cd跳转**

我们实现 [CWD](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp/command/impl/CWD.java) 类做目录跳转, 实际上它的功能是将当期会话中, 用户正在访问的目录做修改
, 这样当我们需要展示用户当前位置时, 可以根据会话得到它的访问目录位置.

**功能: list展示**

这个功能就特别复杂了, 因为 ftp 的 list 调用是要借助主动/被动模式的, 我这里实现了被动模式 [PORT](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp/command/impl/PORT.java)
, 它的原理就是与请求端开辟一个新的通道做数据传输, 协议默认是将各自端口号 +1 来建立新连接.

list 功能的实现类为 [LIST](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp/command/impl/LIST.java), 这个类的执行流程其实是特别复杂的, 因为我们是在原有连接上开辟一个新的连接, 并需要监听及返回响应, 完成后还要及时的关闭连接.

这就要求我们不能使用同步阻塞模型, 否则会非常没有必要的将主线程卡住, 等待有可能是大数据量的传输, 所以这里是大量借用了 Netty 的线程模型机制, 比如 ChannelFuture 的 addListen() 监听, 以及 Promise 的 setSuccess() 通知唤醒机制, 详细使用方式可以查阅代码.



