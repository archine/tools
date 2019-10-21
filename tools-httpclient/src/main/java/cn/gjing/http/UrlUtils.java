package cn.gjing.http;


import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gjing
 **/
public class UrlUtils {

    /**
     * 用户请求连接采用占位符的时候进行url和参数拼接,结果例子为: http://localhost:xx/test/2414/42daf
     * @param url 请求地址
     * @param params 参数
     * @return string
     */
    public static String append(String url,Object...params) {
        StringBuilder builder = new StringBuilder(url);
        if (url.indexOf("/", url.length() - 1) == -1) {
            builder.append("/");
        }
        for (Object param : params) {
            builder.append(param).append("/");
        }
        return builder.toString();
    }


    /**
     * 参数按照字段名的Unicode码从小到大排序（字典序）
     *
     * @param paramMap   要排序的参数
     * @param urlEncode  是否需要URLEncode，true：需要/false：不需要
     * @param keyToLower 是否需要将Key转换为全小写 true:key转化成小写，false:不转化
     * @return 排序后的参数字符串
     */
    public static String paramUnicodeSort(Map<String, ?> paramMap, boolean urlEncode, boolean keyToLower) {
        String buff;
        try {
            List<Map.Entry<String, ?>> infoIds = new ArrayList<>(paramMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            infoIds.sort(Comparator.comparing(Map.Entry::getKey));
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, ?> item : infoIds) {
                if (!StringUtils.isEmpty(item.getKey())) {
                    String key = item.getKey();
                    String val = String.valueOf(item.getValue());
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "UTF-8");
                        key = URLEncoder.encode(key, "UTF-8");
                    }
                    if (keyToLower) {
                        buf.append(key.toLowerCase()).append("=").append(val);
                    } else {
                        buf.append(key).append("=").append(val);
                    }
                    buf.append("&");
                }
            }
            buff = buf.toString();
            if (!StringUtils.isEmpty(buff)) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return buff;
    }

    /**
     * 将URL中的查询参数部分解析成键值对
     *
     * @param url 将url的参数转为map
     * @return map
     */
    public static Map<String, String> toMap(String url) {
        final Map<String, String> queryPairs = new ConcurrentHashMap<>(16);
        final String[] param = url.split("\\?");
        if (param.length<1) {
            return null;
        }
        final String[] pairs = param[1].split("&");
        if (pairs.length<1) {
            return null;
        }
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            String key;
            try {
                key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
                final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1),
                        "UTF-8") : null;
                if (!key.isEmpty()) {
                    queryPairs.put(key, value);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return queryPairs;
    }

}
