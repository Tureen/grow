BIO改进的例子

代码:
```java
package club.tulane.nio.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioDemo {

    public static void main(String[] args) {
        final BioDemo bioDemo = new BioDemo();

        new Thread(() -> {
            try {
                bioDemo.buildService01();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                bioDemo.buildService02();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                bioDemo.buildService03();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        System.out.println("启动完毕");
    }

    /**
     * 主线程同步
     * @throws IOException
     */
    public void buildService01() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8801);
        System.out.println("8801端口就绪");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                printMessage(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 多线程同步
     * @throws IOException
     */
    public void buildService02() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8802);
        System.out.println("8802端口就绪");
        while (true) {
            try {
                final Socket socket = serverSocket.accept();
                new Thread(() -> {
                    printMessage(socket);
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 线程池同步
     * @throws IOException
     */
    public void buildService03() throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(40);
        final ServerSocket serverSocket = new ServerSocket(8803);
        System.out.println("8803端口就绪");
        while (true) {
            try {
                final Socket socket = serverSocket.accept();
                executorService.execute(() -> printMessage(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void printMessage(Socket socket){
        try {
            Thread.sleep(20);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type:text/html;charset=utf-8");
            String body = "hello,nio1";
            printWriter.println("Content-Length:" + body.getBytes().length);
            printWriter.println();
            printWriter.write(body);
            printWriter.close();
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

运行情况
```shell script
Tulane@tulane  ~  wrk -c 40 -d30s http://localhost:8801
Running 30s test @ http://localhost:8801
  2 threads and 40 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   900.18ms  107.37ms 968.32ms   96.48%
    Req/Sec    13.92      8.41    40.00     73.68%
  711 requests in 30.10s, 82.98KB read
  Socket errors: connect 0, read 1281, write 22, timeout 0
Requests/sec:     23.62
Transfer/sec:      2.76KB

 Tulane@tulane  ~  wrk -c 40 -d30s http://localhost:8802
Running 30s test @ http://localhost:8802
  2 threads and 40 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    24.70ms    2.31ms  32.71ms   66.35%
    Req/Sec    69.23     30.20   190.00     68.07%
  4154 requests in 30.07s, 1.38MB read
  Socket errors: connect 0, read 46240, write 16, timeout 0
Requests/sec:    138.14
Transfer/sec:     46.90KB

 Tulane@tulane  ~  wrk -c 40 -d30s http://localhost:8803
Running 30s test @ http://localhost:8803
  2 threads and 40 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    23.00ms    2.33ms  58.31ms   76.00%
    Req/Sec    27.87     19.37   121.00     81.97%
  1575 requests in 30.07s, 1.02MB read
  Socket errors: connect 0, read 48975, write 10, timeout 0
Requests/sec:     52.39
Transfer/sec:     34.74KB
```

从运行上可以看出, 主线程同步接收请求时QPS 23, 多线程时提高到 138, 线程池时为 52

这里线程池的数据并没有比无限 new 线程的方式高, 我的推测是, cpu空闲时间还是过长, 加大线程池内线程能改善情况