package edu.zhiyong;
import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class OnlineUserPool {
    private static int onLineUserNumber=0;
    private static final Map<String, WebSocket> onLineUserMap = new HashMap<String, WebSocket>();

    //get websocket by user name
     static WebSocket getConnectionByName(String userName) {
                return onLineUserMap.get(userName);
    }

    //get username by websocket
     static String getUserNameByConnection(WebSocket connect) {
        Set<String> keySet = onLineUserMap.keySet();
        synchronized (keySet) {
            for (String userName : keySet) {
                WebSocket conn = onLineUserMap.get(connect);
                if (conn.equals(connect)) {
                    return userName;
                }
            }
        }
        return null;
    }

    //add online user in to the pool
    static void addOnlineUser(String userName, WebSocket connect){
        onLineUserMap.put(userName,connect);
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

    /*Need message queue to complete this function ,not completed */
    static void sendMessageToUser(String userName,String message){
        WebSocket conn=onLineUserMap.get(userName);
    if(conn!=null){
        conn.send(message);
    }else{
        //need to complete, offline message should store in mysql data.
    }
    }

}

