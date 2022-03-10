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
{"type":"message","from":"zhiyong","to":"jian","time":"123123","content":"xxxx","messageID":"123132321"}  测试完这里,没注册好友无法发送消息
{"type":"sendFriendRequest","from":"zhiyong","to":"jian","time":"2222222222","content":"somemessage"}
{"type":"approveFriendRequest","from":"jian","to":"zhiyong","time":"345345345"}
{"type":"declineFriendRequest","from":"jian","to":"zhiyong","time":"4536463456","content":"somemessage"}

服务器端回复
{"type":"formatError","content":"Incorrect format"}
{"type":"singUp","content":"User name has been registered"} //用户名已被注册
{"type":"signUp","content":"Registration success"} //注册成功
{"type":"signIn","content":"Login success"}//登录成功
{"type":"signIn","content":"Login fail"}//登录失败

{"type":"verifySender","content":"Please sign in again"}//加入发送方验证

{"type":"friendReRepeat","content":"Please do not send same friend requests within 3 days"}//已经发送的不要重复添加
{"type":"friendReFail","content":"User not found"}  //没有该用户
{"type":"friendAddAlready","content":"You already added this user"} //已经是好友的情况下重复添加
{"type":"friendReSucc","content":"Friend request send"} //成功发送好友邀请

服务器回复



{"type":"FriendAddSuc","from":"jian","to":"zhiyong","time":"09/03/2022"}//成功添加好友 双方同时发送. jian同意并发出
{"type":"FriendAddFail","from":"jian","to":"zhiyong","time":"09/03/2022","content":"Friend request does not exist or expired"}

{"type":"messageFail","to":"jian","messageId":"123132321","content":"not your friend"}//非好友发送信息
{"type":"messageSuccess","to":"jian","messageId":"123132321"}//信息发送成功
*/


