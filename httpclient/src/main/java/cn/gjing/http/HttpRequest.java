package cn.gjing.http;

import cn.gjing.ParamUtil;
import cn.gjing.annotation.ExcludeParam;
import cn.gjing.annotation.NotNull2;
import cn.gjing.enums.HttpStatus;
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
public class HttpRequest {
    private static RestTemplate restTemplate;

    @NotNull2
    public String post(String requestUrl) {
        try {
            checkRequestType(requestUrl);
            return restTemplate.postForEntity(requestUrl, HttpMethod.POST, String.class).getBody();
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }


    @NotNull2
    public String post(String requestUrl, Map<String, String> params) {
        try {
            checkRequestType(requestUrl);
            return restTemplate.postForEntity(requestUrl, mapToMultiValueMap(params), String.class).getBody();
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }


    @NotNull2
    public String post(String requestUrl, @ExcludeParam Map<String, String> params, Map<String, String> headers) {
        try {
            checkRequestType(requestUrl);
            HttpHeaders httpHeaders = new HttpHeaders();
            for (String s : headers.keySet()) {
                httpHeaders.add(s, headers.get(s));
            }
            HttpEntity httpEntity = new HttpEntity<>(null, httpHeaders);
            if (ParamUtil.paramIsNotEmpty(params)) {
                httpEntity = new HttpEntity<>(mapToMultiValueMap(params), httpHeaders);
            }
            return restTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, String.class).getBody();
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }


    @NotNull2
    public String post(String requestUrl, Map<String, String> params, Map<String, String> headers, String proxyIp, String proxyPort) {
        if (!ParamUtil.multiParamHasEmpty(Arrays.asList(proxyIp, proxyPort))) {
            setProxy(proxyIp, proxyPort);
        }
        try {
            checkRequestType(requestUrl);
            HttpEntity<Object> httpEntity;
            if (ParamUtil.paramIsNotEmpty(headers)) {
                HttpHeaders httpHeaders = new HttpHeaders();
                for (String s : headers.keySet()) {
                    httpHeaders.add(s, headers.get(s));
                }
                httpEntity = new HttpEntity<>(null, httpHeaders);
                if (ParamUtil.paramIsNotEmpty(params)) {
                    httpEntity = new HttpEntity<>(mapToMultiValueMap(params), httpHeaders);
                }
                return restTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, String.class).getBody();
            } else {
                if (ParamUtil.paramIsNotEmpty(params)) {
                    return restTemplate.postForEntity(requestUrl, mapToMultiValueMap(params), String.class).getBody();
                } else {
                    return restTemplate.postForEntity(requestUrl, HttpMethod.POST, String.class).getBody();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpException(e.getCause().getMessage());
        }
    }


    @NotNull2
    public String get(String requestUrl) {
        try {
            checkRequestType(requestUrl);
            return restTemplate.getForObject(requestUrl, String.class);
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }


    @NotNull2
    public String get(String requestUrl, Map<String, String> params) {
        try {
            checkRequestType(requestUrl);
            return restTemplate.getForObject(UrlUtil.urlAppend(requestUrl, params), String.class, params);
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }


    @NotNull2
    public String get(String requestUrl, @ExcludeParam Map<String, String> params, Map<String, String> headers) {
        try {
            checkRequestType(requestUrl);
            HttpHeaders httpHeaders = new HttpHeaders();
            for (String s : headers.keySet()) {
                httpHeaders.add(s, headers.get(s));
            }
            HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
            if (ParamUtil.paramIsNotEmpty(params)) {
                return restTemplate.exchange(UrlUtil.urlAppend(requestUrl, params), HttpMethod.GET, httpEntity, String.class, params).getBody();
            } else {
                return restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, String.class).getBody();
            }
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }


    @NotNull2
    public String get(String requestUrl, @ExcludeParam Map<String, String> params, @ExcludeParam Map<String, String> headers, String proxyIp, String proxyPort) {
        if (!ParamUtil.multiParamHasEmpty(Arrays.asList(proxyIp, proxyPort))) {
            setProxy(proxyIp, proxyPort);
        }
        try {
            checkRequestType(requestUrl);
            if (ParamUtil.paramIsNotEmpty(headers)) {
                HttpHeaders httpHeaders = new HttpHeaders();
                for (String s : headers.keySet()) {
                    httpHeaders.add(s, headers.get(s));
                }
                HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
                if (ParamUtil.paramIsNotEmpty(params)) {
                    return restTemplate.exchange(UrlUtil.urlAppend(requestUrl, params), HttpMethod.GET, httpEntity, String.class, params).getBody();
                } else {
                    return restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, String.class).getBody();
                }
            } else {
                if (ParamUtil.paramIsNotEmpty(params)) {
                    return restTemplate.getForObject(UrlUtil.urlAppend(requestUrl, params), String.class, params);
                } else {
                    return restTemplate.getForObject(requestUrl, String.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpException(HttpStatus.BAD_REQUEST.getMsg());
        }
    }

    /**
     * check request type for http or https
     *
     * @param url url
     */
    private void checkRequestType(String url) {
        String[] urlArr = ParamUtil.split(url, ":");
        if (ParamUtil.paramIsNotEmpty(urlArr)) {
            if (Objects.equals(ParamUtil.toLowerCase(urlArr[0]), HttpType.HTTP.getType())) {
                restTemplate = new RestTemplate();
            } else if (Objects.equals(ParamUtil.toLowerCase(urlArr[0]), HttpType.HTTPS.getType())) {
                restTemplate = new RestTemplate(new HttpsClientRequestFactory());
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

    /**
     * set handle
     *
     * @param proxyHost proxy_ip
     * @param proxyPort proxy_port
     */
    private void setProxy(String proxyHost, String proxyPort) {
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
    }
}
