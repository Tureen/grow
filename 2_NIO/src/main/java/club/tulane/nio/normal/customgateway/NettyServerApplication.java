package club.tulane.nio.normal.customgateway;

import club.tulane.nio.normal.customgateway.inbound.HttpInboundServer;
import com.alibaba.fastjson.JSON;

public class NettyServerApplication {

    public final static String GATEWAY_NAME = "NIOGateway";
    public final static String GATEWAY_VERSION = "1.0.0";

    public static void main(String[] args) {
        String[] proxyServices = new String[]{"http://localhost:8808", "http://localhost:8809"};
        final String json = JSON.toJSONString(proxyServices);

        String proxyServer = System.getProperty("proxyServer", json);
        String proxyPort = System.getProperty("proxyPort","8888");

        int port = Integer.parseInt(proxyPort);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" starting...");
        HttpInboundServer server = new HttpInboundServer(port, proxyServer);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" started at http://localhost:" + port + " for server:" + proxyServer);
        try {
            server.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
