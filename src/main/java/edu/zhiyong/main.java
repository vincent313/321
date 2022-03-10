package edu.zhiyong;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;

import java.util.HashMap;
import java.util.Map;


public class main {
    public static void main(String[] args) {
        WebSocketImpl.DEBUG = false;
        int port = 3210; // 端口
        ConnectService Conn = new ConnectService(port);
        Conn.start();
    }
}

/*
用户端发出
{"type":"singup","user":"zhiyong","pas":"cist321"}
{"type":"singin","user":"zhiyong","pas":"cist321"}
{"type":"singup","user":"jian","pas":"cist321"}
{"type":"singin","user":"jian","pas":"cist321"}
{"type":"message","from":"zhiyong","to":"jian","time":"09/03/2022","content":"xxxx","messageID":"123132321"}  测试完这里,没注册好友无法发送消息
{"type":"sendFriendRequest","from":"zhiyong","to":"jian","time":"2222222222","content":"somemessage"}
{"type":"approveFriendRequest","from":"jian","to":"zhiyong","time":"09/03/2022"}
{"type":"declineFriendRequest","from":"jian","to":"zhiyong","time":"09/03/2022","content":"somemessage"}

客户端回复
{"type":"formatError","content":"Incorrect format"}
{"type":"singUp","content":"User name has been registered"}
{"type":"signUp","content":"Registration success"}
{"type":"signIn","content":"Login success"}
{"type":"signIn","content":"Login fail"}

{"type":"verifySender","content":"Please sign in again"}

{"type":"friendReRepeat","content":"Please do not send same friend requests within 3 days"}
{"type":"friendReFail","content":"User not found"}
{"type":"friendReSucc","content":"Friend request send"}

{"type":"FriendAddSuc","from":"jian","to":"zhiyong","time":"09/03/2022"}
{"type":"FriendAddFail","from":"jian","to":"zhiyong","time":"09/03/2022","content":"Friend request does not exist or expired"}

{"type":"messageFail","to":"jian","messageId":"123132321","content":"not your friend"}
{"type":"messageSuccess","to":"jian","messageId":"123132321"}
*/
