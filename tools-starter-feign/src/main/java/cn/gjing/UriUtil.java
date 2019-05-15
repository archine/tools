package cn.gjing;

/**
 * @author Gjing
 **/
class UriUtil {

    private static boolean isAbsolute(String url) {
        return url != null && !url.isEmpty() && url.startsWith("http");
    }

     static String checkUrl(String url) {
        if (isAbsolute(url)) {
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            return url;
        }
        throw new IllegalArgumentException("The url is no absolute");
    }

    static String buildUrl(String url, String methodPath) {
        StringBuilder builder = new StringBuilder();
        builder.append(url);
        if (!methodPath.startsWith("/")) {
            builder.append("/").append(methodPath);
        }
        builder.append(methodPath);
        return builder.toString();
    }
}
