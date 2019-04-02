package cn.gjing.http;

import cn.gjing.ParamUtil;
import org.springframework.util.MultiValueMap;

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
public class UrlUtil {
    /**
     * url拼接:其结果为:http://127.0.0.1:8080/test?param1={param1},这种格式会出现在restTemplate请求的get方法中
     * @param map 参数
     * @param url 需要拼接的url 例如：http://127.0.0.1:8080/test,需要尾随方法名
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
     * url拼接:其结果为:http://127.0.0.1:8080/test?param1={param1}
     *
     * @param map 参数
     * @param url 需要拼接的url 例如：http://127.0.0.1:8080/test,需要尾随方法名
     * @return 拼接完后的url
     */
    public static String urlAppend(String url, Map<String, String> map) {
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


    /**
     * 参数按照字段名的Unicode码从小到大排序（字典序）
     * @param paramMap    要排序的参数
     * @param urlEncode  是否需要URLEncode，true：需要/false：不需要
     * @param keyToLower 是否需要将Key转换为全小写 true:key转化成小写，false:不转化
     * @return 排序后的参数字符串
     */
    public static String unicodeSort(Map<String, String> paramMap, boolean urlEncode, boolean keyToLower) {
        String buff;
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<>(paramMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            infoIds.sort(Comparator.comparing(Map.Entry::getKey));
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (ParamUtil.paramIsNotEmpty(item.getKey())) {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");
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
            if (ParamUtil.paramIsNotEmpty(buf)) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            return null;
        }
        return buff;
    }

    /**
     * 将URL中的查询参数部分解析成键值对
     * @param queryString URL中的查询参数部分，不含前缀'?'
     * @return map
     */
    public static Map<String, String> urlParamToMap(String queryString) {
        final Map<String, String> queryPairs = new ConcurrentHashMap<>(16);
        final String[] pairs = queryString.split("&");
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
