package cn.gjing;

import cn.gjing.annotation.ExcludeParam;
import cn.gjing.annotation.NotNull2;
import cn.gjing.enums.HttpType;
import cn.gjing.ex.HttpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
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
    public static String post(String requestUrl, @ExcludeParam MultiValueMap<String, String> params,@ExcludeParam Map<String, String> headers, @ExcludeParam String proxyIp,
                              @ExcludeParam String proxyPort) {
        if (!ParamUtil.multiParamHasEmpty(Arrays.asList(proxyIp, proxyPort))) {
            setProxy(proxyIp, proxyPort);
        }
        try {
            HttpEntity<Object> httpEntity;
            if (ParamUtil.paramIsNotEmpty(headers)) {
                HttpHeaders httpHeaders = new HttpHeaders();
                for (String s : headers.keySet()) {
                    httpHeaders.add(s, headers.get(s));
                }
                httpEntity = new HttpEntity<>(null, httpHeaders);
                if (ParamUtil.paramIsNotEmpty(params)) {
                    httpEntity = new HttpEntity<>(params, httpHeaders);
                }
                return getRestTemplate(requestUrl).exchange(requestUrl, HttpMethod.POST, httpEntity, String.class).getBody();
            } else {
                if (ParamUtil.paramIsNotEmpty(params)) {
                    return getRestTemplate(requestUrl).postForEntity(requestUrl, params, String.class).getBody();
                } else {
                    return getRestTemplate(requestUrl).postForEntity(requestUrl, HttpMethod.POST, String.class).getBody();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpException(e.getCause().getMessage());
        }
    }

    @NotNull2
    public static String get(String requestUrl,@ExcludeParam Map<String, String> params, @ExcludeParam Map<String, String> headers, @ExcludeParam String proxyIp,
                             @ExcludeParam String proxyPort) {
        if (!ParamUtil.multiParamHasEmpty(Arrays.asList(proxyIp, proxyPort))) {
            setProxy(proxyIp, proxyPort);
        }
        try {
            if (ParamUtil.paramIsNotEmpty(headers)) {
                HttpHeaders httpHeaders = new HttpHeaders();
                for (String s : headers.keySet()) {
                    httpHeaders.add(s, headers.get(s));
                }
                HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
                if (ParamUtil.paramIsNotEmpty(params)) {
                    return getRestTemplate(requestUrl).exchange(UrlUtil.urlAppend(requestUrl, params), HttpMethod.GET, httpEntity, String.class, params).getBody();
                } else {
                    return getRestTemplate(requestUrl).exchange(requestUrl, HttpMethod.GET, httpEntity, String.class).getBody();
                }
            } else {
                if (ParamUtil.paramIsNotEmpty(params)) {
                    return getRestTemplate(requestUrl).getForObject(UrlUtil.urlAppend(requestUrl, params), String.class, params);
                } else {
                    return getRestTemplate(requestUrl).getForObject(requestUrl, String.class);
                }
            }
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }

    /**
     * set handle
     *
     * @param proxyHost proxy_ip
     * @param proxyPort proxy_port
     */
    private static void setProxy(String proxyHost, String proxyPort) {
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
    }

    /**
     * get restTemplate type for http or https
     *
     * @param url url
     */
    private static RestTemplate getRestTemplate(String url) {
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
    private static MultiValueMap<String, String> mapToMultiValueMap(Map<String, String> map) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        for (String s : map.keySet()) {
            multiValueMap.add(s, map.get(s));
        }
        return multiValueMap;
    }
}
