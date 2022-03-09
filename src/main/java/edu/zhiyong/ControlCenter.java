package edu.zhiyong;
import com.google.gson.Gson;
import org.java_websocket.WebSocket;


import java.util.HashMap;
import java.util.Map;

class ControlCenter {
    //should store in database
    static Map<String,String> registeredUserList=new HashMap<String, String>();

    static String messageAnalysis(String message, WebSocket webSocket){
        try{
        Gson gson = new Gson();
        Map<String,String> messageMap=gson.fromJson(message,Map.class);
            switch (messageMap.get("type")){
            case "singup":
                return singUp(messageMap);
            case "singin":
                return singIn(messageMap,webSocket);
            case "message":
                return sendMessage(messageMap);
            case "add":


        }
        return "Incorrect format";
        }
        catch(Exception e){
            return "Incorrect format";
        }
    }

//str="{'type':'singup','user':'zhiyong','pas':'cist321'}";
    static synchronized String singUp(Map<String,String> map){
    String name=map.get("user");
    if(registeredUserList.containsKey(name)){
        return "User name has been registered";
    }else{
        registeredUserList.put(name,map.get("pas"));
         return "Registration success";
    }
    }

//str="{'type':'singin','user':'zhiyong','pas':'cist321'}";
    static String singIn(Map<String,String> map,WebSocket webSocket){
    String username=map.get("user");
    String password=map.get("pas");

    if (registeredUserList.containsKey(username)&& registeredUserList.get(username).equals(password)){
        OnlineUserPool.addOnlineUser(username,webSocket);
        return "login success";
    }else{
        return "login fail";
    }

    }
    //str="{'type':'message','from':'zhiyong','to':'jian','time':'09/03/2022','content':'somemessage'}";
    //For now, just implemented sending messages to registered online users.
    static String sendMessage(Map<String,String> map){
        String to=map.get("to");
    if(registeredUserList.containsKey(to)){
        Gson gson=new Gson();
        String jsonStr = gson.toJson(map);
        OnlineUserPool.sendMessageToUser(to,jsonStr);
        return "ack";
    }else{
        return "Not such user";
    }
    }
}
