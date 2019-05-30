package cn.gjing;

/**
 * @author Gjing
 **/
class UriUtil {

    static String buildUrl(String targetAddress) {
        if (targetAddress.endsWith("/")) {
            targetAddress = targetAddress.substring(0, targetAddress.length() - 1);
        }
        return "http://" + targetAddress;
    }
    static String buildUrl(String targetAddress, String methodPath) {
        StringBuilder builder = new StringBuilder();
        builder.append(targetAddress);
        if (!methodPath.startsWith("/")) {
            builder.append("/").append(methodPath);
        }
        builder.append(methodPath);
        return builder.toString();
    }
}
