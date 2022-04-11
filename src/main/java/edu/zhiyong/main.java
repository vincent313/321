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
/*        System.out.println("server on line, port 1102");
        WebSocketImpl.DEBUG = false;
        int port = 1102; // 端口
        ConnectService Conn = new ConnectService(port);
        Conn.start();*/

        EncryptDecrypt test=new EncryptDecrypt();

            String plain="hello world";
            System.out.println("最初原文:"+plain);

            test.aesGenerateKey();
            String jiamijes =test.aesEncry(plain);
            System.out.println("AES加密后的信息:"+jiamijes);

            String aeskeymingwen=test.getAesKey();
            System.out.println("加密前AES key:"+ aeskeymingwen);

            String jiamihouaeskey=EncryptDecrypt.rsaEncrypt(aeskeymingwen);
            System.out.println("RSA加密后 Aes key:"+ jiamihouaeskey);

            String jiemihouAES= EncryptDecrypt.rsaDecrypt(jiamihouaeskey);
            System.out.println("RSA解密后 Aes key:"+ jiemihouAES);

            test.setAesKey(jiemihouAES);
            System.out.println("-----注入rsa 解密后的AES key----");


            String zuizhong = test.aesDecrypt(jiamijes);
            System.out.println("最终加密后的明文信息: "+zuizhong);


    }


}
