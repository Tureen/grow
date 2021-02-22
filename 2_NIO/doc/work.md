# 作业

**常规** 

 - [X] 1（选做）运行课上的例子，以及 Netty 的例子，分析相关现象。
 - [X] 2（必做）写一段代码，使用 HttpClient 或 OkHttp 访问 http://localhost:8801 ，代码提交到 Github。
 - [ ] 3（必做）整合你上次作业的 httpclient/okhttp；
 - [ ] 4（选做）使用 netty 实现后端 http 访问（代替上一步骤）
 - [ ] 5（必做）实现过滤器。
 - [ ] 6（选做）实现路由。

**进阶**

实现一个http 文件服务器和一个ftp文件服务器。

- [ ] 1)10-实现文件列表展示：http直接网页展示列表即可。ftp支持cd、ls命令。
- [ ] 2)20-实现文件上传下载：http上传不需要支持multi-part，直接post文件内容即可。ftp只需要支持主动模式或被动模式的一种。
- [ ] 3)30-支持断点续传：http下载需要实现range，上传需要自己设计服务器端的分片方式并记录。ftp需要实现retr，stor，rest命令。
- [ ] 4)30-实现多线程文件上传下载：基于断点续传，需考虑客户端分片方式，多线程调度。
- [ ] 5)30-实现爬虫爬取前面实现的服务器上所有文件：需要考虑html解析，记录多个文件的传输进度，位置等。

<br>

# 答题

常规1:[点击打开md文档](https://github.com/Tureen/grow/blob/main/2_NIO/doc/work/normal_1.md)

<br>

常规2:[点击打开md文档](https://github.com/Tureen/grow/blob/main/2_NIO/doc/work/normal_2.md)

<br>

进阶1:[点击打开代码](https://github.com/Tureen/grow/tree/main/2_NIO/src/main/java/club/tulane/nio/advanced/http/Server.java)

<br>
