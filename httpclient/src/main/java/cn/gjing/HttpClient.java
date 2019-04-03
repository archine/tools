package cn.gjing;

import cn.gjing.annotation.ExcludeParam;
import cn.gjing.annotation.NotNull2;
import cn.gjing.enums.HttpType;
import cn.gjing.ex.HttpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

/**
 * @author Gjing
 **/
@Slf4j
public class HttpClient {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final RestTemplate REST_TEMPLATE2 = new RestTemplate(new HttpsClientRequestFactory());

    @NotNull2
    public String post(String requestUrl) {
        return HttpRequest.post(requestUrl, getRestTemplate(requestUrl));
    }

    @NotNull2
    public String post(String requestUrl, Map<String, String> params) {
        return HttpRequest.post(requestUrl, mapToMultiValueMap(params), getRestTemplate(requestUrl));
    }

    @NotNull2
    public String post(String requestUrl,@ExcludeParam Map<String, String> params, Map<String, String> headers) {
        return HttpRequest.post(requestUrl, mapToMultiValueMap(params), headers, getRestTemplate(requestUrl));
    }

    @NotNull2
    public String post(String requestUrl, @ExcludeParam Map<String, String> params,@ExcludeParam Map<String, String> headers, String proxyIp, String proxyPort) {
        return HttpRequest.post(requestUrl, mapToMultiValueMap(params), headers, proxyIp, proxyPort, getRestTemplate(requestUrl));
    }

    @NotNull2
    public String get(String requestUrl) {
        return HttpRequest.get(requestUrl, getRestTemplate(requestUrl));
    }

    @NotNull2
    public String get(String requestUrl, Map<String, String> params) {
        return HttpRequest.get(requestUrl, params, getRestTemplate(requestUrl));
    }

    @NotNull2
    public String get(String requestUrl, @ExcludeParam Map<String, String> params, Map<String, String> headers) {
        return HttpRequest.get(requestUrl, params, headers, getRestTemplate(requestUrl));
    }

    @NotNull2
    public String get(String requestUrl, @ExcludeParam Map<String, String> params, @ExcludeParam Map<String, String> headers, String proxyIp, String proxyPort) {
        return HttpRequest.get(requestUrl, params, headers, proxyIp, proxyPort, getRestTemplate(requestUrl));
    }
    /**
     * get restTemplate type for http or https
     *
     * @param url url
     */
    private RestTemplate getRestTemplate(String url) {
        String[] urlArr = ParamUtil.split(url, ":");
        if (ParamUtil.paramIsNotEmpty(urlArr)) {
            if (Objects.equals(ParamUtil.toLowerCase(urlArr[0]), HttpType.HTTP.getType())) {
                return REST_TEMPLATE;
            } else if (Objects.equals(ParamUtil.toLowerCase(urlArr[0]), HttpType.HTTPS.getType())) {
                return REST_TEMPLATE2;
            } else {
                throw new HttpException("The requested url is invalid, please use an http or https address");
            }
        } else {
            throw new HttpException("The parameter requestUrl cannot be null");
        }
    }

    /**
     * map 转multiValueMap
     *
     * @param map hashMap
     * @return multiValueMap
     */
    private MultiValueMap<String, String> mapToMultiValueMap(Map<String, String> map) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        for (String s : map.keySet()) {
            multiValueMap.add(s, map.get(s));
        }
        return multiValueMap;
    }
}
