package edu.zhiyong;
import org.java_websocket.WebSocketImpl;


public class main {
    public static void main(String[] args) {
        WebSocketImpl.DEBUG = false;
        int port = 3210; // 端口
        ConnectService Conn = new ConnectService(port);
        Conn.start();
    }
}

/*{"type":"singup","user":"zhiyong","pas":"cist321"}
{"type":"singup","user":"jian","pas":"cist321"}
{"type":"singin","user":"zhiyong","pas":"cist321"}
{"type":"singin","user":"jian","pas":"cist321"}
{"type":"message","from":"zhiyong","to":"jian","time":"09/03/2022","content":"somemessage"}*/
