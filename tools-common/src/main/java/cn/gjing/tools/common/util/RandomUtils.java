package cn.gjing.tools.common.util;

import java.util.Random;

/**
 * @author Gjing
 **/
public final class RandomUtils {
    private RandomUtils() {

    }

    private final static Random RANDOM = new Random();

    public static Random getRandom() {
        return RANDOM;
    }

    public static int randomInt() {
        return RANDOM.nextInt();
    }

    public static int randomInt(int max) {
        return RANDOM.nextInt(max);
    }

    public static int randomInt(int min, int max) {
        return randomInt(max) + min;
    }

    /**
     * 生成混合指定长度字符串（字符串和数字）
     * @param length 长度
     * @return s
     */
    public static String generateMixString(int length) {
        String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(allChar.charAt(randomInt(allChar.length())));
        }
        return builder.toString();
    }

    /**
     * 获取指定长度纯字符串
     * @param length 长度
     * @return s
     */
    public static String generateString(int length) {
        String letterStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(letterStr.charAt(randomInt(letterStr.length())));
        }
        return builder.toString();
    }

    public static String generateNumber(int length) {
        String number = "0123456789";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(number.charAt(randomInt(number.length())));
        }
        return builder.toString();
    }
}
