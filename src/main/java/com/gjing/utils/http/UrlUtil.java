package com.gjing.utils.http;

import org.springframework.util.MultiValueMap;

import java.util.Map;

/**
 * @author Archine
 **/
public class UrlUtil {
    /**
     * url拼接
     *
     * @param map 参数
     * @param url 需要拼接的url
     * @return 拼接完后的url
     */
    public static String urlAppend(String url, MultiValueMap<String, String> map) {
        StringBuilder builder = new StringBuilder();
        builder.append(url).append("?");
        for (String s : map.keySet()) {
            if (map.size() == 1) {
                builder.append(s).append("=").append("{").append(s).append("}");
            } else {
                builder.append(s).append("=").append("{").append(s).append("}&");
            }
        }
        return builder.toString().substring(0, builder.toString().length() - 1);
    }

    /**
     * url append , example: http://ip:port?param1={param1}
     * @param map param
     * @param url url
     * @return appended url
     */
    public static String urlAppend(String url, Map<String, Object> map) {
        StringBuilder builder = new StringBuilder();
        builder.append(url).append("?");
        for (String s : map.keySet()) {
            if (map.size() == 1) {
                builder.append(s).append("=").append("{").append(s).append("}");
            } else {
                builder.append(s).append("=").append("{").append(s).append("}&");
            }
        }
        if (map.size() == 1) {
            return builder.toString();
        } else {
            return builder.toString().substring(0, builder.toString().length() - 1);
        }
    }
}
