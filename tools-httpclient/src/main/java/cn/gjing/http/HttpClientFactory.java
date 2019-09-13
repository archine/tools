package cn.gjing.http;

/**
 * httpClient factory
 *
 * @author Gjing
 **/
public class HttpClientFactory {

    private HttpClientFactory() {

    }

    /**
     * create httpClient instance
     *
     * @param requestUrl request url
     * @param method     method
     * @return HttpClient
     */
    public static HttpClient builder(String requestUrl, HttpMethod method) {
        return new HttpClientImpl(requestUrl, method);
    }
}
