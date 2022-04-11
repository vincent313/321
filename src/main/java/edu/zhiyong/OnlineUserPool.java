package edu.zhiyong;
import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.Map;

class OnlineUserPool {
    private static int onLineUserNumber=0;
    private static  Map<String, WebSocket> onLineUserMap = new HashMap<String, WebSocket>();
    private static  Map<WebSocket, String> onLineWebsocketUser = new HashMap<WebSocket, String>();
    //get websocket by user name
     static WebSocket getConnectionByName(String userName) {
                return onLineUserMap.get(userName);
    }
     static String getUserNameByWebsocket(WebSocket webSocket) {
        return onLineWebsocketUser.get(webSocket);    }

    //add online user in to the pool
    static void addOnlineUser(String userName, WebSocket connect){
        if(onLineUserMap.get(userName)!=null)
        {
        removeOnlineUser(onLineUserMap.get(userName));
        removeOnlineUser(userName);
        }
        onLineUserMap.put(userName,connect);
        onLineWebsocketUser.put(connect,userName);
        onLineUserNumber++;
        System.out.println("current online user:"+ onLineUserNumber);
    }

    //remove online user
    static boolean removeOnlineUser(String userName){
         if(onLineUserMap.containsKey(userName)){
             onLineUserMap.remove(userName);
             onLineUserNumber--;
             System.out.println("current online user:"+ onLineUserNumber);
             return true;
         }
         else{
             return false;
         }
    }

    static boolean removeOnlineUser(WebSocket webSocket){

        if(onLineWebsocketUser.containsKey(webSocket)){
            onLineWebsocketUser.remove(webSocket);
            return true;
        }
        else{
            return false;
        }
    }

    /*Need message queue to complete this function ,not completed */
    static void sendMessageToUser(String userName,String message){
        WebSocket conn=onLineUserMap.get(userName);
    if(conn!=null){
        conn.send(message);
    }else{
        //need to complete, offline message should store in mysql data.
    }
    }

    static void sendMessageToUser(WebSocket webSocket,String message){
        webSocket.send(message);
    //need to complete, offline message should store in mysql data.
    }

}

