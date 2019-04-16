package cn.gjing.http2;

import cn.gjing.UrlUtil;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * @author Gjing
 **/
@SuppressWarnings({"rawtypes"})
public class HttpClient2 {
    /**
     * 默认连接超时3000毫秒
     */
    private static final int CONNECT_TIMEOUT = 3000;
    /**
     * 默认读超时 5000毫秒
     */
    private static final int READ_TIMEOUT = 5000;
    /**
     * post请求
     * @param url 请求url
     * @param params 参数
     * @param headers 请求头
     * @param connectTimeout 连接超时时间
     * @param readTimeout 读超时
     * @param responseType 返回类型
     * @param <T> T
     * @return responseType
     */
    
    public static <T> T post(String url, Map<String, ?> params, Map<String, ?> headers, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.invokeUrl(url, params, headers, connectTimeout, readTimeout, HttpMethod.POST, responseType);
    }

    /**
     * post请求
     * @param url 请求url
     * @param params 参数
     * @param connectTimeout 超时时间
     * @param readTimeout 读超时
     * @param responseType 返回类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T post(String url, Map<String, ?> params, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.invokeUrl(url, params, null, connectTimeout, readTimeout, HttpMethod.POST, responseType);
    }

    /**
     * post请求
     * @param url 请求url
     * @param params 参数
     * @param responseType 返回类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T post(String url, Map<String, ?> params, Class<T> responseType) {
        return HttpHandle.invokeUrl(url, params, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.POST, responseType);
    }

    /**
     * post请求
     * @param url 请求url
     * @param responseType 返回类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T post(String url, Class<T> responseType) {
        return HttpHandle.invokeUrl(url, null, null, CONNECT_TIMEOUT, READ_TIMEOUT,
                HttpMethod.POST, responseType);
    }

    /**
     * post请求
     * @param url 请求url
     * @param params 参数
     * @param headers 请求头
     * @param responseType 返回类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T post(String url, Map<String, ?> params, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.invokeUrl(url, params, headers, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.POST, responseType);
    }

    /**
     * GET请求
     * @param url 请求url
     * @param params 参数
     * @param headers 请求头
     * @param connectTimeout 连接超时时间
     * @param readTimeout 读超时
     * @param responseType 返回类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T get(String url, Map<String, ?> params, Map<String, ?> headers, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.invokeUrl(url, params, headers, connectTimeout, readTimeout, HttpMethod.GET, responseType);

    }

    /**
     * get请求
     * @param url 请求url
     * @param params 参数
     * @param connectTimeout 连接超时时间
     * @param readTimeout 读超时
     * @param responseType 返回类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T get(String url, Map<String, ?> params, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.invokeUrl(url, params, null, connectTimeout, readTimeout, HttpMethod.GET, responseType);
    }

    /**
     * get请求
     * @param url 请求url
     * @param params 参数
     * @param responseType 响应类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T get(String url, Map<String, ?> params, Class<T> responseType) {
        return HttpHandle.invokeUrl(url, params, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.GET, responseType);
    }

    /**
     * get请求
     * @param url 请求url
     * @param params 参数
     * @param headers 请求头
     * @param responseType 响应类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T get(String url, Map<String, ?> params, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.invokeUrl(url, params, headers, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.GET, responseType);
    }

    /**
     * get请求,用于直接URL占位符请求
     * @param url 请求url
     * @param responseType 响应类型
     * @param params 参数
     * @param <T> T
     * @return responseType
     */
    public static <T> T get(String url, Class<T> responseType, Object... params) {
        return HttpHandle.invokeUrl(UrlUtil.urlAppend(url,params), null, null, CONNECT_TIMEOUT, READ_TIMEOUT,
                HttpMethod.GET, responseType);
    }

    /**
     * get请求
     * @param url 请求url
     * @param responseType 响应类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T get(String url, Class<T> responseType) {
        return HttpHandle.invokeUrl(url, null, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.GET, responseType);
    }

    /**
     * PUT请求
     * @param url 请求连接
     * @param params 参数
     * @param connectTimeout 连接超时时间
     * @param readTimeout 读超时
     * @param responseType 响应类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T put(String url, Map<String, ?> params, Integer connectTimeout, Integer readTimeout,Class<T> responseType) {
        return HttpHandle.invokeUrl(url, params, null, connectTimeout, readTimeout, HttpMethod.PUT,responseType);
    }

    /**
     * PUT请求
     * @param url 请求url
     * @param params 参数
     * @param headers 请求头
     * @param connectTimeout 超时时间
     * @param readTimeout 读超时
     * @param responseType 响应类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T put(String url, Map<String, ?> params, Map<String,?> headers,Integer connectTimeout, Integer readTimeout,Class<T> responseType) {
        return HttpHandle.invokeUrl(url, params, headers, connectTimeout, readTimeout, HttpMethod.PUT,responseType);
    }

    /**
     * PUT请求
     * @param url 请求url
     * @param params 请求头
     * @param responseType 响应类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T put(String url, Map<String, ?> params,Class<T> responseType) {
        return HttpHandle.invokeUrl(url,params, null, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.PUT,responseType);
    }

    /**
     * PUT请求
     * @param url 请求url
     * @param responseType 响应类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T put(String url,Class<T> responseType) {
        return HttpHandle.invokeUrl(url,null, null, CONNECT_TIMEOUT, READ_TIMEOUT,
                HttpMethod.PUT,responseType);
    }

    /**
     * PUT请求
     * @param url 请求url
     * @param params 参数
     * @param headers 请求头
     * @param responseType 响应类型
     * @param <T> T
     * @return responseType
     */
    public static<T> T put(String url, Map<String, ?> params, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.invokeUrl(url, params, headers, CONNECT_TIMEOUT,READ_TIMEOUT, HttpMethod.PUT, responseType);
    }

    /**
     * PUT请求
     * @param url 请求url
     * @param params 参数
     * @param responseType 响应类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T put(String url, Class<T> responseType, Object... params) {
        return HttpHandle.invokeUrl(UrlUtil.urlAppend(url,params), null, null, CONNECT_TIMEOUT, READ_TIMEOUT,
                HttpMethod.PUT, responseType);
    }

    /**
     * delete请求
     * @param url 请求URl
     * @param params 参数
     * @param connectTimeout 超时时间
     * @param readTimeout 参数
     * @param responseType 返回类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T delete(String url, Map<String, ?> params, Integer connectTimeout, Integer readTimeout, Class<T>responseType) {
        return HttpHandle.invokeUrl(url, params, null, connectTimeout, readTimeout, HttpMethod.DELETE,responseType);
    }

    /**
     * delete请求
     * @param url 请求URl
     * @param headers 请求头
     * @param params 参数
     * @param connectTimeout 超时时间
     * @param readTimeout 参数
     * @param responseType 返回类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T delete(String url, Map<String, ?> params, Map<String, ?> headers, Integer connectTimeout, Integer readTimeout, Class<T> responseType) {
        return HttpHandle.invokeUrl(url, params, headers, connectTimeout, readTimeout, HttpMethod.DELETE,responseType);
    }

    /**
     * delete请求
     * @param url 请求URl
     * @param params 参数
     * @param responseType 返回类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T delete(String url, Map<String, ?> params, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.invokeUrl(url, params, headers, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.DELETE, responseType);
    }

    /**
     * delete请求
     * @param url 请求URl
     * @param responseType 返回类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T delete(String url, Map<String, ?> headers, Class<T> responseType) {
        return HttpHandle.invokeUrl(url, null, headers, CONNECT_TIMEOUT, READ_TIMEOUT, HttpMethod.DELETE, responseType);
    }

    /**
     * delete请求
     * @param url 请求URl
     * @param params 参数
     * @param responseType 返回类型
     * @param <T> T
     * @return responseType
     */
    public static <T> T delete(String url, Class<T> responseType, Object... params) {
        return HttpHandle.invokeUrl(UrlUtil.urlAppend(url,params), null, null, CONNECT_TIMEOUT, READ_TIMEOUT,
                HttpMethod.DELETE, responseType);
    }
}
