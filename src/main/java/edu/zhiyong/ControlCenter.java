package edu.zhiyong;
import com.google.gson.Gson;
import org.java_websocket.WebSocket;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

class ControlCenter {
    //should store in database ,user name and password
    static HashMap<String,String> registeredUserList=new HashMap<String, String>();

    //store friend request(not responded), delete in 3 days, user cant send request again in 3 days.
    static HashMap<String, Long> friendRequestList =new HashMap<String,Long>();

    //friend relationship table. key => user name, value =>user friend list
    static HashMap<String , HashSet<String>> userRelation= new HashMap<String,HashSet<String>>();

    static void messageAnalysis(String message, ConnectService conn){
        try{
        Gson gson = new Gson();

           // System.out.println(messageMap.get("type"));
        Map<String,String> messageMap=gson.fromJson(message,Map.class);

            switch (messageMap.get("type")){
            case "signup":
                 signup(messageMap,conn);
                 return;
            case "login":
                 login(messageMap,conn);
                 return;
            case "message":
                         sendMessage(messageMap,conn);
                 return;
            case "sendFriendRequest":
                 sendFriendRequest(messageMap,conn);
                 return;
            case "approveFriendRequest":
                  approveFriendRequest(messageMap,conn);
                  return;
            case "declineFriendRequest":
                  declineFriendRequest(messageMap,conn);
                  return;
            }
            respond(conn,"{\"type\":\"formatError\",\"content\":\"Incorrect format\"}");

            return;
                    }
        catch(Exception e){

            respond(conn,"{\"type\":\"formatError\",\"content\":\"Incorrect format\"}");

            }
    }

//str="{'type':'signup','user':'zhiyong','pas':'cist321'}";
    static synchronized void signup(Map<String,String> map,ConnectService conn){
    String name=map.get("user");
    if(registeredUserList.containsKey(name)){
        respond(conn,"{\"type\":\"signUp\",\"content\":\"User name has been registered\"}");
    }else{
        registeredUserList.put(name,map.get("pas"));
        // create user friend list
        HashSet<String> friendList=new HashSet<String>();
        userRelation.put(name,friendList);
        respond(conn,"{\"type\":\"signUp\",\"content\":\"Registration success\"}");
    }
    }

//str="{'type':'singin','user':'zhiyong','pas':'cist321'}";
    static void login(Map<String,String> map,ConnectService conn){

    String username=map.get("user");
    String password=map.get("pas");

    if (registeredUserList.containsKey(username)&& registeredUserList.get(username).equals(password)){
        OnlineUserPool.addOnlineUser(username,conn.getWebsocket());

        respond(conn,"{\"type\":\"login\",\"content\":\"Login success\"}");
    }else{

        respond(conn,"{\"type\":\"login\",\"content\":\"Login fail\"}");
    }

    }

    /*
    1.verify sender is sender
    2.verify user relationship
    3.send message*/
    //str="{'type':'message','from':'zhiyong','to':'jian','time':'09/03/2022','content':'somemessage'}";
    static void sendMessage(Map<String,String> map,ConnectService conn){
        if (!verifySender(map, conn)) {

            return;}
             String to=map.get("to");
             String from=map.get("from");


        Gson gson=new Gson();
        if (userRelation.get(from).contains(to)){
            String jsonStr = gson.toJson(map);
            respond(to,jsonStr);
            map.put("type","messageSuccess");
            String ack=gson.toJson(map);
            respond(conn,ack);
        }else{
            map.put("type","messageFail");
            map.put("content","not your friend");
            String ack=gson.toJson(map);
            respond(conn,ack);
        }
    }


    //send friend request, user can only initiate the same request once in a three-day period
    static synchronized void sendFriendRequest(Map<String,String> map,ConnectService conn){
        String sender=map.get("from");
        String receiver=map.get("to");
        String requestID=sender+receiver;

        if (!verifySender(map, conn) ){return;}

        if(!registeredUserList.containsKey(map.get("to"))){
           // System.out.println("here");
            respond(conn,"{\"type\":\"friendReFail\",\"content\":\"User not found\"}");
            return;
        }

        if(userRelation.get(receiver).contains(sender)){
            respond(conn,"{\"type\":\"friendAddAlready\",\"content\":\"You already added this user\"}");
            return;
        }

        if(friendRequestList.containsKey(requestID)){
            respond(conn,"{\"type\":\"friendReRepeat\",\"content\":\"Please do not send same friend requests within 3 days\"}");
            return;
        }else
        {
        Long timeStamp=Long.parseLong(map.get("time"));
        friendRequestList.put(requestID,timeStamp);
        String to=map.get("to");
        Gson gson=new Gson();
        String jsonStr = gson.toJson(map);
        respond(to,jsonStr);
        respond(conn,"{\"type\":\"friendReSucc\",\"content\":\"Friend request send\"}");
        }

    }
    /*verify sender, and check friend request list(friend request expire in 3 days).
    If A approve B friend, put A in to B friend list, B to A friends,then notice them*/
    static synchronized void approveFriendRequest(Map<String,String> map,ConnectService conn){
        if (!verifySender(map, conn)){return;}
        String sender=map.get("from");
        String receiver=map.get("to");
        String request=receiver+sender;
        if(friendRequestList.containsKey(request)){
            userRelation.get(sender).add(receiver);
            userRelation.get(receiver).add(sender);
            friendRequestList.remove(request);
            map.put("type","FriendAddSuc");
            String currentTime=Long.toString(System.currentTimeMillis());
            map.put("time",currentTime);
            Gson gson=new Gson();
            String jsonStr = gson.toJson(map);
            respond(conn,jsonStr);
            respond(receiver,jsonStr);
            return;
        }else{
            map.put("type","FriendAddFail");
            String currentTime=Long.toString(System.currentTimeMillis());
            map.put("time",currentTime);
            map.put("content","Friend request does not exist or expired");
            Gson gson=new Gson();
            String jsonStr = gson.toJson(map);
            respond(conn,jsonStr);
        }

    }

    static synchronized void declineFriendRequest(Map<String,String> map,ConnectService conn){
        if (!verifySender(map,  conn)){return;}
        String sender=map.get("from");
        String receiver=map.get("to");
        String request=receiver+sender;
        if(friendRequestList.containsKey(request)){
            friendRequestList.remove(request);
            return;
        }else{
            String currentTime=Long.toString(System.currentTimeMillis());
            map.put("time",currentTime);
            map.put("content","Friend request does not exist or expired");
            map.put("type","FriendAddFail");
            Gson gson=new Gson();
            String jsonStr = gson.toJson(map);
            respond(conn,jsonStr);
        }
    }

    static void respond( ConnectService conn,String message){
        try {
            String s=EncryptDecrypt.aesEncry(message,conn.getAESKEY());
            OnlineUserPool.sendMessageToUser(conn.getWebsocket(),s);
            System.out.println("Server respond to client plain text: "+message);
            System.out.println("Server respond to client encrypted by AES key text: "+s);

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

    }

    static void respond(String username,String message){

        OnlineUserPool.sendMessageToUser(username,message);
    }

    static boolean verifySender (Map<String,String> map,ConnectService conn){
        String from=map.get("from");
        if(OnlineUserPool.getUserNameByWebsocket(conn.getWebsocket()).equals(from))
        {
            return true;
        }
        else
            respond(conn,"{\"type\":\"verifySender\",\"content\":\"Please log in again\"}");
        return false;
    }

}
