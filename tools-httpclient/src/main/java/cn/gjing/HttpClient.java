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
     * @param <T>            T
     * @return responseType
     */

    public static <T> T post(String url, Map<String, ?> queryMap, Map<String, ?> headers, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.of(responseType, headers).invokeUrl(url, queryMap, null, connectTimeout, readTimeout, HttpMethod.POST);
    }

    /**
     * post请求
     *
     * @param url            请求url
     * @param queryMap       参数
     * @param connectTimeout 超时时间
     * @param readTimeout    读超时
     * @param responseType   返回类型
     * @param <T>            T
     * @return responseType
     */
    public static <T> T post(String url, Map<String, ?> queryMap, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.of(responseType, null).invokeUrl(url, queryMap, null, connectTimeout, readTimeout, HttpMethod.POST);
    }

    /**
     * post请求
     *
     * @param url          请求url
     * @param queryMap     参数
     * @param responseType 返回类型
     * @param <T>          T
     * @return responseType
     */
    public static <T> T post(String url, Map<String, ?> queryMap, Class<T> responseType) {
        return HttpHandle.of(responseType, null).invokeUrl(url, queryMap, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.POST);
    }

    /**
     * post发送body请求
     *
     * @param url          请求url
     * @param body         body内容
     * @param responseType 响应类型
     * @param <T>          T
     * @return T
     */
    public static <T> T post(String url, Object body, Class<T> responseType) {
        return HttpHandle.of(responseType, null).invokeUrl(url, null, body, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.POST);
    }

    /**
     * post发送body请求
     *
     * @param url          请求url
     * @param body         body内容
     * @param headers      请求头
     * @param responseType 响应类型
     * @param <T>          T
     * @return T
     */
    public static <T> T post(String url, Object body, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.of(responseType, headers).invokeUrl(url, null, body, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.POST);
    }

    /**
     * post请求
     *
     * @param url          请求url
     * @param responseType 返回类型
     * @param <T>          T
     * @return responseType
     */
    public static <T> T post(String url, Class<T> responseType) {
        return HttpHandle.of(responseType, null).invokeUrl(url, null, null, CONNECT_TIMEOUT, READ_TIMEOUT,
                HttpMethod.POST);
    }

    /**
     * post请求
     *
     * @param url          请求url
     * @param queryMap     参数
     * @param headers      请求头
     * @param responseType 返回类型
     * @param <T>          T
     * @return responseType
     */
    public static <T> T post(String url, Map<String, ?> queryMap, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.of(responseType, headers).invokeUrl(url, queryMap, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.POST);
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
     * @param <T>            T
     * @return responseType
     */
    public static <T> T get(String url, Map<String, ?> queryMap, Map<String, ?> headers, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.of(responseType, headers).invokeUrl(url, queryMap, null, connectTimeout, readTimeout, HttpMethod.GET);
    }

    /**
     * get请求
     *
     * @param url            请求url
     * @param queryMap       参数
     * @param connectTimeout 连接超时时间
     * @param readTimeout    读超时
     * @param responseType   返回类型
     * @param <T>            T
     * @return responseType
     */
    public static <T> T get(String url, Map<String, ?> queryMap, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.of(responseType, null).invokeUrl(url, queryMap, null, connectTimeout, readTimeout, HttpMethod.GET);
    }

    /**
     * get请求
     *
     * @param url          请求url
     * @param queryMap     参数
     * @param responseType 响应类型
     * @param <T>          T
     * @return responseType
     */
    public static <T> T get(String url, Map<String, ?> queryMap, Class<T> responseType) {
        return HttpHandle.of(responseType, null).invokeUrl(url, queryMap, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.GET);
    }

    /**
     * get请求
     *
     * @param url          请求url
     * @param queryMap     参数
     * @param headers      请求头
     * @param responseType 响应类型
     * @param <T>          T
     * @return responseType
     */
    public static <T> T get(String url, Map<String, ?> queryMap, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.of(responseType, headers).invokeUrl(url, queryMap, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.GET);
    }

    /**
     * get请求,用于直接URL占位符请求
     *
     * @param url          请求url
     * @param responseType 响应类型
     * @param queryMap     参数
     * @param <T>          T
     * @return responseType
     */
    public static <T> T get(String url, Class<T> responseType, Object... queryMap) {
        return HttpHandle.of(responseType, null).invokeUrl(UrlUtil.urlAppend(url, queryMap), null, null, CONNECT_TIMEOUT, READ_TIMEOUT,
                HttpMethod.GET);
    }

    /**
     * get请求
     *
     * @param url          请求url
     * @param responseType 响应类型
     * @param <T>          T
     * @return responseType
     */
    public static <T> T get(String url, Class<T> responseType) {
        return HttpHandle.of(responseType, null).invokeUrl(url, null, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.GET);
    }

    /**
     * PUT请求
     *
     * @param url            请求连接
     * @param queryMap       参数
     * @param connectTimeout 连接超时时间
     * @param readTimeout    读超时
     * @param responseType   响应类型
     * @param <T>            T
     * @return responseType
     */
    public static <T> T put(String url, Map<String, ?> queryMap, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.of(responseType, null).invokeUrl(url, queryMap, null, connectTimeout, readTimeout, HttpMethod.PUT);
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
     * @param <T>            T
     * @return responseType
     */
    public static <T> T put(String url, Map<String, ?> queryMap, Map<String, ?> headers, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.of(responseType, headers).invokeUrl(url, queryMap, null, connectTimeout, readTimeout, HttpMethod.PUT);
    }

    /**
     * PUT请求
     *
     * @param url          请求url
     * @param queryMap     请求头
     * @param responseType 响应类型
     * @param <T>          T
     * @return responseType
     */
    public static <T> T put(String url, Map<String, ?> queryMap, Class<T> responseType) {
        return HttpHandle.of(responseType, null).invokeUrl(url, queryMap, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.PUT);
    }

    /**
     * PUT请求
     *
     * @param url          请求url
     * @param responseType 响应类型
     * @param <T>          T
     * @return responseType
     */
    public static <T> T put(String url, Class<T> responseType) {
        return HttpHandle.of(responseType, null).invokeUrl(url, null, null, CONNECT_TIMEOUT, READ_TIMEOUT,
                HttpMethod.PUT);
    }

    /**
     * PUT请求
     *
     * @param url          请求url
     * @param queryMap     参数
     * @param headers      请求头
     * @param responseType 响应类型
     * @param <T>          T
     * @return responseType
     */
    public static <T> T put(String url, Map<String, ?> queryMap, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.of(responseType, headers).invokeUrl(url, queryMap, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.PUT);
    }

    /**
     * PUT请求
     *
     * @param url          请求url
     * @param queryMap     参数
     * @param responseType 响应类型
     * @param <T>          T
     * @return responseType
     */
    public static <T> T put(String url, Class<T> responseType, Object... queryMap) {
        return HttpHandle.of(responseType, null).invokeUrl(UrlUtil.urlAppend(url, queryMap), null, null, CONNECT_TIMEOUT, READ_TIMEOUT,
                HttpMethod.PUT);
    }

    /**
     * delete请求
     *
     * @param url            请求URl
     * @param queryMap       参数
     * @param connectTimeout 超时时间
     * @param readTimeout    参数
     * @param responseType   返回类型
     * @param <T>            T
     * @return responseType
     */
    public static <T> T delete(String url, Map<String, ?> queryMap, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.of(responseType, null).invokeUrl(url, queryMap, null, connectTimeout, readTimeout, HttpMethod.DELETE);
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
     * @param <T>            T
     * @return responseType
     */
    public static <T> T delete(String url, Map<String, ?> queryMap, Map<String, ?> headers, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.of(responseType, headers).invokeUrl(url, queryMap, null, connectTimeout, readTimeout, HttpMethod.DELETE);
    }

    /**
     * delete请求
     *
     * @param url          请求URl
     * @param queryMap     参数
     * @param headers      请求头
     * @param responseType 返回类型
     * @param <T>          T
     * @return responseType
     */
    public static <T> T delete(String url, Map<String, ?> queryMap, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.of(responseType, headers).invokeUrl(url, queryMap, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.DELETE);
    }

    /**
     * delete请求
     *
     * @param url          请求URl
     * @param responseType 返回类型
     * @param headers      请求头
     * @param <T>          T
     * @return responseType
     */
    public static <T> T delete(String url, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.of(responseType, headers).invokeUrl(url, null, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.DELETE);
    }

    /**
     * delete请求
     *
     * @param url          请求URl
     * @param queryMap     参数
     * @param responseType 返回类型
     * @param <T>          T
     * @return responseType
     */
    public static <T> T delete(String url, Class<T> responseType, Object... queryMap) {
        return HttpHandle.of(responseType, null).invokeUrl(UrlUtil.urlAppend(url, queryMap), null, null, CONNECT_TIMEOUT, READ_TIMEOUT,
                HttpMethod.DELETE);
    }
}
