package edu.zhiyong;


import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;

 class ConnectService extends WebSocketServer {
    static int connectionNumber=0;
    String userName=null;

     ConnectService(int port) {
        super(new InetSocketAddress(port));

    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        connectionNumber++;
        System.out.println("Current Connection number:"+connectionNumber);
    }

    // if registered client left, remove client from online user list.
    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        connectionNumber--;
    if (userName!=null){
        OnlineUserPool.removeOnlineUser(userName);
    };
        System.out.println("Current Connection number:"+connectionNumber);
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        String replayFromControlCenter=ControlCenter.messageAnalysis(message,webSocket);
        webSocket.send(replayFromControlCenter);

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }
}
