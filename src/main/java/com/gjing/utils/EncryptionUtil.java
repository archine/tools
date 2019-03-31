package com.gjing.utils;

import com.gjing.ex.ParamException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Gjing
 **/
public class EncryptionUtil {

    /**
     * MD5 encryption
     *
     * @param body need to encryption
     * @return encrypted string
     */
    public static String md5(String body) {
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
     * @param source 要编码的字符串
     * @return 编码过的字符串
     */
    public static String encodeBase64(String source) {
        Class<?> clazz;
        Method encodeMethod;
        try {
            clazz = Class.forName("org.apache.commons.codec.binary.Base64");
            encodeMethod = clazz.getMethod("encodeBase64", byte[].class);
            return new String((byte[]) encodeMethod.invoke(null, (Object) source.getBytes()));
        } catch (ClassNotFoundException e) {
            String vm = System.getProperty("java.vm.name");
            System.out.println(vm);
            try {
                if ("Dalvik".equals(vm)) {
                    clazz = Class.forName("android.util.Base64");
                    encodeMethod = clazz.getMethod("encode", byte[].class, int.class);
                    return new String((byte[]) encodeMethod.invoke(null, source.getBytes(), 0));
                } else {
                    clazz = Class.forName("sun.misc.BASE64Encoder");
                    encodeMethod = clazz.getMethod("encode", byte[].class);
                    return (String) encodeMethod.invoke(clazz.newInstance(), (Object) source.getBytes());
                }
            } catch (Exception e1) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * BASE64解码
     * @param encodeSource 编码过的字符串
     * @return 编码前的字符串
     */
    public static String decodeBase64(String encodeSource) {
        Class<?> clazz;
        Method decodeMethod;
        try {
            clazz = Class.forName("org.apache.commons.codec.binary.Base64");
            decodeMethod = clazz.getMethod("decodeBase64", byte[].class);
            System.out.println("decodeBASE64-->" + clazz);
            System.out.println("decodeMethod-->" + decodeMethod);
            return new String((byte[]) decodeMethod.invoke(null, (Object) encodeSource.getBytes()));
        } catch (ClassNotFoundException e) {
            String vm = System.getProperty("java.vm.name");
            System.out.println(vm);
            try {
                if ("Dalvik".equals(vm)) {
                    clazz = Class.forName("android.util.Base64");
                    decodeMethod = clazz.getMethod("decode", byte[].class, int.class);
                    System.out.println("decodeBASE64-->" + clazz);
                    System.out.println("decodeMethod-->" + decodeMethod);
                    return new String((byte[]) decodeMethod.invoke(null, encodeSource.getBytes(), 0));
                } else {
                    clazz = Class.forName("sun.misc.BASE64Decoder");
                    decodeMethod = clazz.getMethod("decodeBuffer", String.class);
                    System.out.println("decodeBASE64-->" + clazz);
                    System.out.println("decodeMethod-->" + decodeMethod);
                    return new String((byte[]) decodeMethod.invoke(clazz.newInstance(), encodeSource));
                }
            } catch (Exception e1) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * sha256 Hmac加密
     *
     * @param str    需要加密的消息
     * @param secret 秘钥
     * @return 加密后的字符串
     */
    public static String sha256Hmac(String str, String secret) {
        String hash;
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] bytes = sha256Hmac.doFinal(str.getBytes(StandardCharsets.UTF_8));
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
            throw new ParamException("Encryption abnormal");
        }
        return hash;
    }

    /**
     *
     * @param str 需要加密的内容
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
     * 将加密后的字节数组转换成字符串
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
