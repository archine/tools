package cn.gjing.tools.feign;

import com.google.gson.Gson;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

/**
 * @author Gjing
 **/
public class FeignClientUtil<T> {

    /**
     * 响应类型
     */
    private final Class<T> responseType;

    /**
     * 路由类型，url or name
     */
    private final RouteType routeType;

    /**
     * 目标地址，可以是服务名型:http://name/ 也可以是url型：http://127.0.0.1:8080/
     */
    private final String targetAddress;
    /**
     * 请求结果
     */
    private T result;

    private static final FeignProcess FEIGN_PROCESS = (FeignProcess) BeanUtil.getBean(Bean.FEIGN_PROCESS.getBeanName());

    private static final Gson GSON = new Gson();

    private FeignClientUtil(Class<T> responseType, RouteType routeType, String targetAddress) {
        this.responseType = Objects.requireNonNull(responseType);
        this.result = null;
        this.routeType = Objects.requireNonNull(routeType);
        this.targetAddress = Objects.requireNonNull(UriUtil.buildUrl(targetAddress));
    }

    /**
     * 实例化FeignClientUtil
     *
     * @param responseType  响应类型
     * @param routeType     路由类型
     * @param <T>           T
     * @param targetAddress 目标地址:可以是: 服务名 或者 127.0.0.1:8090
     * @return FeignClientUtil
     */
    public static <T> FeignClientUtil<T> of(Class<T> responseType, RouteType routeType, String targetAddress) {
        return new FeignClientUtil<>(responseType, routeType, targetAddress);
    }

    /**
     * 默认使用服务名路由,返回值为String
     *
     * @param targetAddress 目标地址,例如: http://服务名/
     * @return FeignClientUtil
     */
    public static FeignClientUtil<String> ofByName(String targetAddress) {
        return new FeignClientUtil<>(String.class, RouteType.NAME, targetAddress);
    }

    /**
     * 默认使用URL路由,返回值为String
     *
     * @param targetAddress 目标地址,例如: 127.0.0.1:8090
     * @return FeignClientUtil
     */
    public static FeignClientUtil<String> ofByUrl(String targetAddress) {
        return new FeignClientUtil<>(String.class, RouteType.URL, targetAddress);
    }

    /**
     * 执行
     *
     * @param httpMethod 请求类型
     * @param queryMap   参数，不需要传null
     * @param methodPath 接口路径, /test/method
     * @return FeignClientUtil
     */
    public FeignClientUtil<T> execute(HttpMethod httpMethod, Map<String, ?> queryMap,
                                      String methodPath) {
        Objects.requireNonNull(httpMethod);
        FeignBean feignBean = routeType.equals(RouteType.URL) ?
                FEIGN_PROCESS.getByUrl(targetAddress) :
                FEIGN_PROCESS.getByName(targetAddress);
        try {
            request(httpMethod, queryMap, null, UriUtil.buildUrl(targetAddress, methodPath), feignBean);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 执行post请求
     *
     * @param jsonEntity json对象或者json字符串
     * @param methodPath 接口路径, /test/method
     * @return FeignClientUtil
     */
    public FeignClientUtil<T> executeByJsonEntity(Object jsonEntity, String methodPath) {
        FeignBean feignBean = routeType.equals(RouteType.URL) ?
                FEIGN_PROCESS.getByUrl(targetAddress) :
                FEIGN_PROCESS.getByName(targetAddress);
        try {
            request(HttpMethod.POST, null, jsonEntity, UriUtil.buildUrl(targetAddress, methodPath), feignBean);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 获取结果
     *
     * @return result
     */
    public T getResult() {
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
     * @param httpMethod 请求类型
     * @param queryMap   参数
     * @param body       body
     * @param url        请求url
     * @param feignBean  feignBean
     * @throws URISyntaxException uri转换异常
     */
    private void request(HttpMethod httpMethod, Map<String, ?> queryMap, Object body, String url,
                         FeignBean feignBean) throws URISyntaxException {
        switch (httpMethod) {
            case POST:
                if (queryMap == null) {
                    if (body == null) {
                        cast(feignBean.post(new URI(url)));
                        break;
                    }
                    cast(feignBean.postByJsonEntity(new URI(url), body));
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
                throw new IllegalStateException("This type is not supported");
        }
    }
}
