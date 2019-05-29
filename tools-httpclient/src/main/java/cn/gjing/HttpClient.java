package cn.gjing;

import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * @author Gjing
 **/
@SuppressWarnings({"rawtypes"})
public class HttpClient {

    /**
     * 默认连接超时5秒
     */
    private static final int CONNECT_TIMEOUT = 5000;
    /**
     * 默认读超时10秒
     */
    private static final int READ_TIMEOUT = 10000;
    

    /**
     * post请求
     *
     * @param url            请求url
     * @param queryMap       参数
     * @param headers        请求头
     * @param connectTimeout 连接超时时间
     * @param readTimeout    读超时
     * @param responseType   返回类型
     * @return responseType
     */

    public <T> T post(String url, Map<String, ?> queryMap, Map<String, ?> headers, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, queryMap, headers, null, connectTimeout, readTimeout, HttpMethod.POST, responseType);
    }

    /**
     * post请求
     *
     * @param url            请求url
     * @param queryMap       参数
     * @param connectTimeout 超时时间
     * @param readTimeout    读超时
     * @param responseType   返回类型
     * @return responseType
     */
    public <T> T post(String url, Map<String, ?> queryMap, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, queryMap, null, null, connectTimeout, readTimeout, HttpMethod.POST, responseType);
    }

    /**
     * post请求
     *
     * @param url          请求url
     * @param queryMap     参数
     * @param responseType 返回类型
     * @return responseType
     */
    public <T> T post(String url, Map<String, ?> queryMap, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, queryMap, null, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.POST, responseType);
    }

    /**
     * post发送body请求
     *
     * @param url          请求url
     * @param jsonEntity  json对象,可以是json字符串对应的对象也可以是map
     * @param responseType 响应类型
     * @return T
     */
    public <T> T postByJsonEntity(String url, Object jsonEntity, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, null, null, jsonEntity, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.POST, responseType);
    }

    /**
     * post发送body请求
     *
     * @param url          请求url
     * @param jsonEntity  json对象,可以是json字符串对应的对象也可以是map
     * @param responseType 响应类型
     * @param headers 请求头
     * @return T
     */
    public <T> T postByJsonEntity(String url, Object jsonEntity,Map<String,?>headers, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, null, headers, jsonEntity, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.POST, responseType);
    }

    /**
     * post发送body请求
     *
     * @param url          请求url
     * @param jsonStr   json字符串
     * @param responseType 响应类型
     * @return T
     */
    public <T> T postByJson(String url, String jsonStr, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, null, null, jsonStr, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.POST, responseType);
    }

    /**
     * post发送body请求
     *
     * @param url          请求url
     * @param jsonStr  json字符串
     * @param responseType 响应类型
     * @param headers 请求头
     * @return T
     */
    public <T> T postByJson(String url, String jsonStr,Map<String,?> headers, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, null, headers, jsonStr, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.POST, responseType);
    }

    /**
     * post发送body请求
     *
     * @param url          请求url
     * @param body         body内容
     * @param headers      请求头
     * @param responseType 响应类型
     * @return T
     */
    public <T> T post(String url, Object body, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, null, headers, body, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.POST, responseType);
    }

    /**
     * post请求
     *
     * @param url          请求url
     * @param responseType 返回类型
     * @return responseType
     */
    public <T> T post(String url, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, null, null, null, CONNECT_TIMEOUT, READ_TIMEOUT,
                HttpMethod.POST, responseType);
    }

    /**
     * post请求
     *
     * @param url          请求url
     * @param queryMap     参数
     * @param headers      请求头
     * @param responseType 返回类型
     * @return responseType
     */
    public <T> T post(String url, Map<String, ?> queryMap, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, queryMap, headers, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.POST, responseType);
    }

    /**
     * GET请求
     *
     * @param url            请求url
     * @param queryMap       参数
     * @param headers        请求头
     * @param connectTimeout 连接超时时间
     * @param readTimeout    读超时
     * @param responseType   返回类型
     * @return responseType
     */
    public <T> T get(String url, Map<String, ?> queryMap, Map<String, ?> headers, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, queryMap, headers, null, connectTimeout, readTimeout, HttpMethod.GET, responseType);
    }

    /**
     * get请求
     *
     * @param url            请求url
     * @param queryMap       参数
     * @param connectTimeout 连接超时时间
     * @param readTimeout    读超时
     * @param responseType   返回类型
     * @return responseType
     */
    public <T> T get(String url, Map<String, ?> queryMap, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, queryMap, null, null, connectTimeout, readTimeout, HttpMethod.GET, responseType);
    }

    /**
     * get请求
     *
     * @param url          请求url
     * @param queryMap     参数
     * @param responseType 响应类型
     * @return responseType
     */
    public <T> T get(String url, Map<String, ?> queryMap, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, queryMap, null, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.GET, responseType);
    }

    /**
     * get请求
     *
     * @param url          请求url
     * @param queryMap     参数
     * @param headers      请求头
     * @param responseType 响应类型
     * @return responseType
     */
    public <T> T get(String url, Map<String, ?> queryMap, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, queryMap, headers, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.GET, responseType);
    }

    /**
     * get请求,用于直接URL占位符请求
     *
     * @param url          请求url
     * @param responseType 响应类型
     * @param queryMap     参数
     * @return responseType
     */
    public <T> T get(String url, Class<T> responseType, Object... queryMap) {
        return HttpHandle.getInstance().invokeUrl(UrlUtil.urlAppend(url, queryMap), null, null, null, CONNECT_TIMEOUT, READ_TIMEOUT,
                HttpMethod.GET, responseType);
    }

    /**
     * get请求
     *
     * @param url          请求url
     * @param responseType 响应类型
     * @return responseType
     */
    public <T> T get(String url, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, null, null, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.GET, responseType);
    }

    /**
     * PUT请求
     *
     * @param url            请求连接
     * @param queryMap       参数
     * @param connectTimeout 连接超时时间
     * @param readTimeout    读超时
     * @param responseType   响应类型
     * @return responseType
     */
    public <T> T put(String url, Map<String, ?> queryMap, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, queryMap, null, null, connectTimeout, readTimeout, HttpMethod.PUT, responseType);
    }

    /**
     * PUT请求
     *
     * @param url            请求url
     * @param queryMap       参数
     * @param headers        请求头
     * @param connectTimeout 超时时间
     * @param readTimeout    读超时
     * @param responseType   响应类型
     * @return responseType
     */
    public <T> T put(String url, Map<String, ?> queryMap, Map<String, ?> headers, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, queryMap, headers, null, connectTimeout, readTimeout, HttpMethod.PUT, responseType);
    }

    /**
     * PUT请求
     *
     * @param url          请求url
     * @param queryMap     请求头
     * @param responseType 响应类型
     * @return responseType
     */
    public <T> T put(String url, Map<String, ?> queryMap, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, queryMap, null, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.PUT, responseType);
    }

    /**
     * PUT请求
     *
     * @param url          请求url
     * @param responseType 响应类型
     * @return responseType
     */
    public <T> T put(String url, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, null, null, null, CONNECT_TIMEOUT, READ_TIMEOUT,
                HttpMethod.PUT, responseType);
    }

    /**
     * PUT请求
     *
     * @param url          请求url
     * @param queryMap     参数
     * @param headers      请求头
     * @param responseType 响应类型
     * @return responseType
     */
    public <T> T put(String url, Map<String, ?> queryMap, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, queryMap, headers, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.PUT, responseType);
    }

    /**
     * PUT请求
     *
     * @param url          请求url
     * @param queryMap     参数
     * @param responseType 响应类型
     * @return responseType
     */
    public <T> T put(String url, Class<T> responseType, Object... queryMap) {
        return HttpHandle.getInstance().invokeUrl(UrlUtil.urlAppend(url, queryMap), null, null, null, CONNECT_TIMEOUT, READ_TIMEOUT,
                HttpMethod.PUT, responseType);
    }

    /**
     * delete请求
     *
     * @param url            请求URl
     * @param queryMap       参数
     * @param connectTimeout 超时时间
     * @param readTimeout    参数
     * @param responseType   返回类型
     * @return responseType
     */
    public <T> T delete(String url, Map<String, ?> queryMap, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, queryMap, null, null, connectTimeout, readTimeout, HttpMethod.DELETE, responseType);
    }

    /**
     * delete请求
     *
     * @param url            请求URl
     * @param headers        请求头
     * @param queryMap       参数
     * @param connectTimeout 超时时间
     * @param readTimeout    参数
     * @param responseType   返回类型
     * @return responseType
     */
    public <T> T delete(String url, Map<String, ?> queryMap, Map<String, ?> headers, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, queryMap, headers, null, connectTimeout, readTimeout, HttpMethod.DELETE, responseType);
    }

    /**
     * delete请求
     *
     * @param url          请求URl
     * @param queryMap     参数
     * @param headers      请求头
     * @param responseType 返回类型
     * @return responseType
     */
    public <T> T delete(String url, Map<String, ?> queryMap, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, queryMap, headers, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.DELETE, responseType);
    }

    /**
     * delete请求
     *
     * @param url          请求URl
     * @param responseType 返回类型
     * @param headers      请求头
     * @return responseType
     */
    public <T> T delete(String url, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.getInstance().invokeUrl(url, null, headers, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.DELETE, responseType);
    }

    /**
     * delete请求
     *
     * @param url          请求URl
     * @param queryMap     参数
     * @param responseType 返回类型
     * @return responseType
     */
    public <T> T delete(String url, Class<T> responseType, Object... queryMap) {
        return HttpHandle.getInstance().invokeUrl(UrlUtil.urlAppend(url, queryMap), null, null, null, CONNECT_TIMEOUT, READ_TIMEOUT,
                HttpMethod.DELETE, responseType);
    }
}
