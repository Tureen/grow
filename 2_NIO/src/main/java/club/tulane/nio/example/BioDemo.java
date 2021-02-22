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
