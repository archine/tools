package cn.gjing.http;


import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author Gjing
 **/
public class UrlUtils {

    /**
     * User request connection url and parameters when using the placeholder, examples results for: http://localhost:xx/test/2414/42daf
     *
     * @param url    Request url
     * @param params params
     * @return url
     */
    public static String append(String url, Object... params) {
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
     * Parameters are sorted by Unicode code of field names from smallest to largest (in lexicographical order)
     *
     * @param paramMap   param map
     * @param urlEncode  Do you need URLEncode? True: yes /false: no
     * @param keyToLower Need to convert Key to all lowercase true: convert Key to all lowercase, false: do not convert
     * @return Param str
     */
    public static String paramUnicodeSort(Map<String, ?> paramMap, boolean urlEncode, boolean keyToLower) {
        String buff;
        try {
            List<Map.Entry<String, ?>> infoIds = new ArrayList<>(paramMap.entrySet());
            infoIds.sort(Comparator.comparing(Map.Entry::getKey));
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
     * Parse the query parameter part of the URL into key-value pairs
     *
     * @param url Url
     * @return map Param map
     */
    public static Map<String, String> toMap(String url) {
        final Map<String, String> queryPairs = new HashMap<>(16);
        final String[] param = url.split("\\?");
        if (param.length < 1) {
            return null;
        }
        final String[] pairs = param[1].split("&");
        if (pairs.length < 1) {
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
