package cn.gjing;

import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

/**
 * @author Gjing
 **/
public class FeignClientUtil<T> {


    private static final FeignProcess FEIGN_PROCESS = (FeignProcess) BeanUtil.getBean("feignProcess");

    private static final Gson GSON = new Gson();
    /**
     * 响应类型
     */
    private final Class<T> responseType;

    /**
     * 路由类型，url or name
     */
    private final RouteType routeType;

    /**
     * 服务名或url
     */
    private final String targetAddress;
    /**
     * 请求结果
     */
    private T result;

    private FeignClientUtil(Class<T> responseType, RouteType routeType, String targetAddress) {
        this.responseType = Objects.requireNonNull(responseType);
        this.result = null;
        this.routeType = Objects.requireNonNull(routeType);
        this.targetAddress = Objects.requireNonNull(targetAddress);
    }

    /**
     * 实例化FeignClientUtil
     *
     * @param responseType  响应类型
     * @param routeType     路由类型
     * @param <T>           T
     * @param targetAddress 目标地址:可以是: http://服务名/ 或者 http://127.0.0.1:8090/
     * @return FeignClientUtil
     */
    public static <T> FeignClientUtil<T> of(Class<T> responseType, RouteType routeType, String targetAddress) {
        return new FeignClientUtil<>(responseType, routeType, UriUtil.checkUrl(targetAddress));
    }

    /**
     * 默认使用服务名路由,返回值为String
     * @param targetAddress 目标地址,例如: http://服务名/
     * @return FeignClientUtil
     */
    public static FeignClientUtil<String> defaultByName(String targetAddress) {
        return new FeignClientUtil<>(String.class, RouteType.NAME, UriUtil.checkUrl(targetAddress));
    }

    /**
     * 默认使用URL路由,返回值为String
     * @param targetAddress 目标地址,例如: http://127.0.0.1:8090/
     * @return FeignClientUtil
     */
    public static FeignClientUtil<String> defaultByUrl(String targetAddress) {
        return new FeignClientUtil<>(String.class, RouteType.URL, UriUtil.checkUrl(targetAddress));
    }

    /**
     * 执行
     *
     * @param requestType 请求类型
     * @param queryMap    参数，不需要传null
     * @param body        body参数，如果不需要传null
     * @param methodPath  接口路径, /test/method
     * @return FeignClientUtil
     * @throws URISyntaxException uri转换异常
     */
    public FeignClientUtil<T> execute(RequestType requestType, Map<String, ?> queryMap, Object body, String methodPath) throws URISyntaxException {
        Objects.requireNonNull(requestType);
        FeignBean feignBean = routeType.equals(RouteType.URL) ? FEIGN_PROCESS.getByUrl(targetAddress) : FEIGN_PROCESS.getByName(targetAddress);
        request(requestType, queryMap, body, targetAddress + methodPath, feignBean);
        return this;
    }

    /**
     * 获取结果
     *
     * @return result
     */
    public T getResult() {
        Objects.requireNonNull(this.result);
        return this.result;
    }

    /**
     * 数据转换
     *
     * @param s 字符串
     */
    @SuppressWarnings("unchecked")
    private void cast(String s) {
        try {
            this.result = GSON.fromJson(s, responseType);
        } catch (Exception e) {
            this.result = (T) s;
        }
    }

    /**
     * 发起请求
     *
     * @param requestType 请求类型
     * @param queryMap    参数
     * @param body        body
     * @param url         请求url
     * @param feignBean   feignBean
     * @throws URISyntaxException uri转换异常
     */
    private void request(RequestType requestType, Map<String, ?> queryMap, Object body, String url, FeignBean feignBean) throws URISyntaxException {
        switch (requestType) {
            case POST:
                if (queryMap == null) {
                    if (body == null) {
                        cast(feignBean.post(new URI(url)));
                        break;
                    }
                    cast(feignBean.postBody(new URI(url), body));
                    break;
                }
                cast(feignBean.post(new URI(url), queryMap));
                break;
            case GET:
                if (queryMap == null) {
                    cast(feignBean.get(new URI(url)));
                    break;
                }
                cast(feignBean.get(new URI(url), queryMap));
                break;
            case DELETE:
                if (queryMap == null) {
                    cast(feignBean.delete(new URI(url)));
                    break;
                }
                cast(feignBean.delete(new URI(url), queryMap));
                break;
            case PUT:
                if (queryMap == null) {
                    cast(feignBean.put(new URI(url)));
                    break;
                }
                cast(feignBean.put(new URI(url), queryMap));
                break;
            case PATCH:
                if (queryMap == null) {
                    cast(feignBean.patch(new URI(url)));
                    break;
                }
                cast(feignBean.patch(new URI(url), queryMap));
                break;
            default:
        }
    }
}
