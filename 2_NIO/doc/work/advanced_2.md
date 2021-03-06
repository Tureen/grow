# 进阶作业2

<br>

## 简介

**题目**: 实现文件上传下载：http上传不需要支持multi-part，直接post文件内容即可。ftp只需要支持主动模式或被动模式的一种。

**思路**: 
1. HTTP服务的上传下载很好解决, 借助请求类型判断, POST 执行上传, GET 则执行查看或下载, 上个题目时查看已完成
2. FTP的上传和下载又比较麻烦了, 都是要借助主动/被动模式的, 且执行模式也要设计成异步回调, 我们借助上个题目时 LIST 命令的实现思路来做

<br>

## HTTP 服务器介绍

代码目录在 [club.tulane.nio.advanced.http](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/http) 包下

http服务器启动代码: [点击打开代码](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/http/Server.java)

<br>

## FTP 服务器介绍

代码目录在 [club.tulane.nio.advanced.ftp](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp) 包下

ftp服务器启动代码: [点击打开代码](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp/Server.java)

**功能: 下载**

FTP 的下载功能在类 [RETR](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp/command/impl/RETR.java)中
, 它最终是在新开辟的连接中, 将文件数据通过 RandomAccessFile 随机读写文件流, 传递给连接的通道.

**功能: 上传**

FTP 的上传功能在类 [STOR](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp/command/impl/RETR.java)中
, 但其实际功能是在 [FtpPortDataClient](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/ftp/FtpPortDataClient.java) 里

这个类的作用就是在被动模式下与请求方建立新的连接通道, 它的内部类 PortDataClientHandler 作为 Inbound 功能接收请求方的文件流, 并通过 RandomAccessFile 随机读写文件流, 将数据写入 FTP 服务器内