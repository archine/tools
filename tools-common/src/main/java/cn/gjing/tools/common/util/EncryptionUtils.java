package cn.gjing.tools.common.util;

import cn.gjing.tools.common.exception.ParamValidException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author Gjing
 **/
public final class EncryptionUtils {
    private EncryptionUtils() {

    }

    /**
     * MD5 encryption
     *
     * @param body need to encryption
     * @return encrypted string
     */
    public static String encodeMd5(String body) {
        StringBuilder buf = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(body.getBytes());
            byte[] b = md.digest();
            int i;
            for (byte b1 : b) {
                i = b1;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return buf.toString();
    }

    /**
     * BASE64编码
     *
     * @param content 要编码的字符串
     * @return 编码过的字符串
     */
    public static String encodeBase64(String content) {
        return Base64.encodeBase64String(content.getBytes());
    }

    /**
     * BASE64解码
     *
     * @param content 编码过的字符串
     * @return 编码前的字符串
     */
    public static String decodeBase64(String content) {
        return new String(Base64.decodeBase64(content));
    }

    /**
     * sha256 Hmac加密
     *
     * @param str    需要加密的消息
     * @param secret 秘钥
     * @return 加密后的字符串
     */
    public static String encodeSha256Hmac(String str, String secret) {
        String hash;
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] bytes = sha256Hmac.doFinal(str.getBytes(StandardCharsets.UTF_8));
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
            throw new ParamValidException("Encryption abnormal");
        }
        return hash;
    }

    /**
     * @param str    需要加密的内容
     * @param secret 秘钥
     * @return str 加密后的字符串
     */
    public static String sha1Hmac(String str, String secret) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(str.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加密
     *
     * @param content 待加密内容
     * @param key     加密的密钥
     * @return 加密后字符串
     */
    public static String encodeAes(String content, String key) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key.getBytes(StandardCharsets.UTF_8));
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] byteRresult = cipher.doFinal(byteContent);
            StringBuilder sb = new StringBuilder();
            for (byte b : byteRresult) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                sb.append(hex.toUpperCase());
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param content 待解密内容
     * @param key     解密的密钥
     * @return 解密后
     */
    public static String decodeAes(String content, String key) {
        if (content.length() < 1) {
            return null;
        }
        byte[] byteResult = new byte[content.length() / 2];
        for (int i = 0; i < content.length() / 2; i++) {
            int high = Integer.parseInt(content.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(content.substring(i * 2 + 1, i * 2 + 2), 16);
            byteResult[i] = (byte) (high * 16 + low);
        }
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key.getBytes(StandardCharsets.UTF_8));
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] result = cipher.doFinal(byteResult);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成密钥对
     */
    public static void genRsaKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        System.out.println("公钥: " + new String(Base64.encodeBase64(rsaPublicKey.getEncoded())));
        System.out.println("私钥：" + new String(Base64.encodeBase64((rsaPrivateKey.getEncoded()))));
    }

    /**
     * 加密
     *
     * @param str       字符串
     * @param publicKey 公钥
     * @return 密文
     */
    public static String encodeRsa(String str, String publicKey) throws Exception {
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.encodeBase64String(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 解密
     *
     * @param str        字符串
     * @param privateKey 私钥
     * @return 解密后字符串
     */
    public static String decodeRsa(String str, String privateKey) throws Exception {
        byte[] inputByte = Base64.decodeBase64(str.getBytes(StandardCharsets.UTF_8));
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte));
    }

    /**
     * 将二进制转换成16进制
     *
     * @param b 字节数组
     * @return 字符串
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

}
