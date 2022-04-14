package edu.zhiyong;
import org.apache.commons.lang3.ArrayUtils;
import org.java_websocket.WebSocketImpl;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class main {
    public static void main(String[] args) throws Exception {
        System.out.println("server on line, port 1102");
        System.out.println("Client generate and using new AES everytime when client login");
        WebSocketImpl.DEBUG = false;
        int port = 1102; // 端口
        ConnectService Conn = new ConnectService(port);
        Conn.start();



    }


}
