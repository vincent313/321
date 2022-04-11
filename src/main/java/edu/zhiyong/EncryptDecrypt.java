package edu.zhiyong;

import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

/*
When client sign up , client encrypt and send to the server their AES key(using server RSA key)
Server store client AES key, when client log in (RSA), server new this instance with client AES key.

So
1. Server dont need to generate AES key(get it from client)
2. Server only need DecryptRSA() function.
3. Server call rsaGenerateKeyPair()function when RsaPrivatekey is null.
4. Because server only need to decrypt message(which include client AES key)
*/
public class EncryptDecrypt {
    private String userName=null;
    private IvParameterSpec iv=new IvParameterSpec(new byte[16]);
    private byte[] AesKey=null;
    private static final String AES_TYPE="AES/CBC/NoPadding";
    private static final int AES_Block_Size=16;
    private static String RsaPublickey = null;
    private static String RsaPrivatekey = null;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAesKey() {
        return new String(Base64.getEncoder().encode(AesKey));
    }

    public void setAesKey(String str) {
        AesKey = Base64.getDecoder().decode(str);
    }

    protected void aesGenerateKey(){
        SecureRandom sr=null;
        // Generate random instance, in SHA1PRNG algorithm not exist, using new secureRandom  (NativePRNG algorithm)
        try {
            sr=SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            sr=new SecureRandom();
        }
        //set seed
        //sr.setSeed(Calendar.getInstance().getTimeInMillis());

        // generate 128bit(16 byte) aes key
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128, sr);
             AesKey = keyGen.generateKey().getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private byte[] padding(byte [] b) throws UnsupportedEncodingException {

        int padsize;
        //calculate how many bit need to pad
        if(b.length%AES_Block_Size==0){
            padsize=AES_Block_Size;
        }else{
            padsize=(AES_Block_Size-(b.length%AES_Block_Size));
        }
        // generate padding array
        byte[] padinfo=new byte[padsize];
        for(int i=0;i<padsize;i++){
            padinfo[i]=inttobyte(padsize);
        }
        //combine two arrary
        byte [] combine =(byte[]) ArrayUtils.addAll(b,padinfo);

        return combine;
    }

    private String unPadding(byte[] b){
        int index= (b.length-b[(b.length-1)]);
        byte [] a= Arrays.copyOfRange(b,0,index);
        return new String(a);
    }

    private byte inttobyte (int x){
        return (byte)x;
    }

    private int bytetoint(byte b){
        return b&0xFF;
    }

    protected String aesEncry(String s) throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        SecretKeySpec keySpec = new SecretKeySpec(AesKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        byte[] byteContent = s.getBytes("utf-8");
        byteContent=padding(byteContent);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec,iv);
        return new String(Base64.getEncoder().encode(cipher.doFinal(byteContent)));
    }

    protected String aesDecrypt(String s) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec keySpec = new SecretKeySpec(AesKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");


        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec,iv);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        byte[] result = cipher.doFinal(Base64.getDecoder().decode(s.getBytes()));

        return unPadding(result);
    }

    public static void rsaGenerateKeyPair() throws Exception {
        File file=new File("publicKey.pem");
        File file1=new File("privateKey.pem");
        if (!file.exists()&&!file1.exists()){

            //KeyPairGenerator instance use for generate public and private key
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(1024,new SecureRandom());
            //generate key pair and store in key pair
            KeyPair keyPair = keyPairGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();//得到私钥
            PublicKey publicKey = keyPair.getPublic();//得到公钥
            //get public key
            String publicKeyString=new String(Base64.getEncoder().encode(publicKey.getEncoded()));
            //get private key
            String privateKeyString=new String(Base64.getEncoder().encode(privateKey.getEncoded()));
            //create new file
            file.createNewFile();
            file1.createNewFile();

            //write public and private key in to local file system
            FileWriter fileWritter = new FileWriter(file.getName(),true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(publicKeyString);

            FileWriter fileWritter1 =new FileWriter(file1.getName(),true);
            BufferedWriter bufferWritter1 = new BufferedWriter(fileWritter1);
            bufferWritter1.write(privateKeyString);
            //close bufferWritter and fileWriter
            bufferWritter.close();
            bufferWritter1.close();
            fileWritter1.close();
            fileWritter.close();
            RsaPublickey=publicKeyString;
            RsaPrivatekey=privateKeyString;

        }
        else{
            //if key file exist , read file from local file system.
            int publicKeyLength= (int) file.length();
            int privateKeyLength= (int) file1.length();
            byte[] publicKeyContent = new byte[publicKeyLength];
            byte [] privateContent =new byte[privateKeyLength];
            FileInputStream in=new FileInputStream(file);
            FileInputStream in1=new FileInputStream(file1);
            in.read(publicKeyContent);
            in1.read(privateContent);

            RsaPublickey = new String(publicKeyContent);
            RsaPrivatekey = new String(privateContent);
            in.close();
            in1.close();
        }
    }

    public static String rsaEncrypt(String str) throws Exception {
        if (RsaPublickey==null|RsaPrivatekey==null){
            rsaGenerateKeyPair();
        }

        //get string public key in byte[]
        byte[] decodedPublicKey = Base64.getDecoder().decode(RsaPublickey);

        // generate public key instance
        RSAPublicKey pubKey = (RSAPublicKey)KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedPublicKey));
        //generate cipher instance(RSA mode)
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(1, pubKey);
        //get RSA encrypted byte[] file
        byte[] outBytes = cipher.doFinal(str.getBytes("UTF-8"));
        //encode byte[] to string(BASE64)
        String outStr =new String(Base64.getEncoder().encode(outBytes));
        return outStr;
    }

    protected static String rsaDecrypt(String str) throws Exception {
        //if key == null, generate key pair
        if (RsaPublickey==null|RsaPrivatekey==null){
            rsaGenerateKeyPair();
        }
        // all encrypted message are encode by Base64, so decoder first
        byte [] input = Base64.getDecoder().decode(str);
        // get private key in byte[]
        byte[] privateKeyInBytes=Base64.getDecoder().decode(RsaPrivatekey);
        //generate key instance
        PrivateKey priKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyInBytes));
        //generate cipher
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(2, priKey);
        //decrypt , get plain byte[] , trans to String
        String outStr = new String(cipher.doFinal(input));
        return outStr;
    }
}
