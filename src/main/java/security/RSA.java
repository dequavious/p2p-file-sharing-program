package security;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class RSA {

    private String PUBLIC_KEY_STRING;
    private String PRIVATE_KEY_STRING;
    private String EXT_PUBLIC_KEY_STRING;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private PublicKey extPublicKey;

    public RSA() {
        init();
    }

    private void init() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
            initStrings();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void initExtPublicKey(String key) {
        try {
            X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(key));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            extPublicKey = keyFactory.generatePublic(keySpecPublic);

            EXT_PUBLIC_KEY_STRING = key;

        } catch (Exception ignored) {

        }
    }

    public String getPublicKeyString() {
        return PUBLIC_KEY_STRING;
    }

    private void initStrings() {
        PUBLIC_KEY_STRING = encode(publicKey.getEncoded());
        PRIVATE_KEY_STRING = encode(privateKey.getEncoded());
    }

    public void printKeys(String extKeyOwner) {
        System.out.println("Public key: " + PUBLIC_KEY_STRING);
        System.out.println("Private key: " + PRIVATE_KEY_STRING + "\n");
        System.out.println(extKeyOwner + " public key: " + EXT_PUBLIC_KEY_STRING + "\n");
    }

    public String encrypt(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, extPublicKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return encode(encryptedBytes);
    }

    public String decrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = decode(data);
        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
        return new String(decryptedMessage);
    }

    private String encode(byte[] data) {
        return new Base64().encodeToString(data);
    }

    private byte[] decode(String data) {
        return new Base64().decode(data);
    }
}
