package edu.zhiyong;


import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

class ConnectService extends WebSocketServer {
    static int connectionNumber=0;
    String userName=null;
    private String AESKEY=null;
    private WebSocket websocket;

     ConnectService(int port) {
        super(new InetSocketAddress(port));

    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
         this.websocket=webSocket;
        connectionNumber++;
        System.out.println("Current Connection number:"+connectionNumber);
    }

    // if registered client left, remove client from online user list.
    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        connectionNumber--;
    if (userName!=null){
        OnlineUserPool.removeOnlineUser(userName);
        OnlineUserPool.removeOnlineUser(webSocket);
    };
        webSocket.close();
        this.AESKEY=null;
        System.out.println("websocket connection closed");
        System.out.println("Current Connection number:"+connectionNumber);

    }

    public WebSocket getWebsocket(){
         return this.websocket;
    }

    public String getAESKEY(){
         return AESKEY;
    }
    @Override
    public void onMessage(WebSocket webSocket, String message) {

        if (this.AESKEY==null){

            try {
                this.AESKEY=EncryptDecrypt.rsaDecrypt(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Client "+connectionNumber+" Send AES KEY(Encrypt by server RSA public key) :"+message);
            System.out.println("Client "+connectionNumber+ " Send AES KEY(Decrypt by server RSA private key) :"+AESKEY);
            return;
        }

        try {

            String s=EncryptDecrypt.aesDecrypt(message,AESKEY);
            System.out.println("Client "+connectionNumber+" Send message(Encrypt by AES key) :"+message);
            System.out.println("Client "+connectionNumber+ " Send mess(Decrypt by AES key) :"+s);
            ControlCenter.messageAnalysis(s,this);

        } catch (NoSuchPaddingException e) {

            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        } catch (BadPaddingException e) {

            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {

            e.printStackTrace();
        }

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }
}
