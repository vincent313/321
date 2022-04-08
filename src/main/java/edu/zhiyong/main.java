package edu.zhiyong;
import org.java_websocket.WebSocketImpl;

public class main {
    public static void main(String[] args) {
        System.out.println("server on line, port 1102");
        WebSocketImpl.DEBUG = false;
        int port = 1102; // 端口
        ConnectService Conn = new ConnectService(port);
        Conn.start();
    }
}


