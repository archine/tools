package cn.gjing.http;

import java.util.Map;

/**
 * http client
 *
 * @author Gjing
 **/
public interface HttpClient  {
    /**
     * carry request header
     *
     * @param header request header map
     * @return HttpClient
     */
    HttpClient withHeader(Map<String, String> header);

    /**
     * carry request parameter map
     *
     * @param queryMap parameter map
     * @return HttpClient
     */
    HttpClient withParam(Map<String, ?> queryMap);

    /**
     * carry request body
     *
     * @param json json string or json object
     * @return HttpClient
     */
    HttpClient withBody(Object json);

    /**
     * send request
     *
     * @param responseType response type
     * @param <T>          T
     * @return T
     */
    <T> T execute(Class<T> responseType);
}
