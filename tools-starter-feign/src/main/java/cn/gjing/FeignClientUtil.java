package cn.gjing;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

/**
 * @author Gjing
 **/
@Component
public class FeignClientUtil<T> {
    @Resource
    private FeignProcess feignProcess;
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
     * 请求结果
     */
    private T result;

    private FeignClientUtil(Class<T> responseType,RouteType routeType) {
        this.responseType = Objects.requireNonNull(responseType);
        this.result = null;
        this.routeType = Objects.requireNonNull(routeType);
    }

    /**
     * 实例化FeignClientUtil
     * @param responseType 响应类型
     * @param routeType 路由类型
     * @param <T> T
     * @return FeignClientUtil
     */
    public static <T> FeignClientUtil<T> of(Class<T> responseType,RouteType routeType) {
        return new FeignClientUtil<>(responseType,routeType);
    }

    /**
     * 执行
     * @param requestType 请求类型
     * @param queryMap 参数，不需要传null
     * @param body body参数，如果不需要传null
     * @param url 请求地址，如果是服务名类型：http://name/methodName
     * @return FeignClientUtil
     * @throws URISyntaxException uri转换异常
     */
    public FeignClientUtil<T> execute(RequestType requestType, Map<String, ?> queryMap, Object body, String url,FeignBean feignBean) throws URISyntaxException {
        if (url == null || requestType == null) {
            throw new NullPointerException("Target or url or requestType cannot be null");
        }
        request(requestType, queryMap, body, url, feignBean);
        return this;
    }

    /**
     * 获取结果
     * @return result
     */
    public T getResult() {
        Objects.requireNonNull(this.result);
        return this.result;
    }

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
     * @param requestType
     * @param queryMap
     * @param body
     * @param url
     * @param feignBean
     * @throws URISyntaxException
     */
    private void request(RequestType requestType, Map<String, ?> queryMap, Object body, String url, FeignBean feignBean) throws URISyntaxException {
        switch (requestType) {
            case POST:
                if (queryMap==null) {
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
