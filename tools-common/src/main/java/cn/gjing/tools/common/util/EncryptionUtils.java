package cn.gjing.tools.common.util;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Gjing
 **/
public final class EncryptionUtils {
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
     * BASE64 encode
     *
     * @param content content
     * @return encrypted
     */
    public static String encodeBase64(String content) {
        return Base64.encodeBase64String(content.getBytes());
    }

    /**
     * BASE64 decode
     *
     * @param content content
     * @return decrypted
     */
    public static String decodeBase64(String content) {
        return new String(Base64.decodeBase64(content));
    }

}
