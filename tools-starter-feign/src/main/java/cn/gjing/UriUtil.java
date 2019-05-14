package cn.gjing;

/**
 * @author Gjing
 **/
public class UriUtil {

    public static boolean isAbsolute(String url) {
        return url != null && !url.isEmpty() && url.startsWith("http://");
    }

    public static String checkUrl(String url) {
        if (isAbsolute(url)) {
            if (url.endsWith("/")) {
                return url;
            } else {
                return url + "/";
            }
        }
        throw new IllegalArgumentException("The url is no absolute");
    }
}
