package cn.gjing;

import cn.gjing.annotation.ExcludeParam;
import cn.gjing.ex.HttpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Gjing
 **/
@Slf4j
class HttpRequest {
    static String post(String requestUrl, RestTemplate restTemplate) {
        try {
            return restTemplate.postForEntity(requestUrl, HttpMethod.POST, String.class).getBody();
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }

    static String post(String requestUrl, MultiValueMap<String, String> param, RestTemplate restTemplate) {
        try {
            return restTemplate.postForEntity(requestUrl, param, String.class).getBody();
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }


    static String post(String requestUrl, MultiValueMap<String, String> params, Map<String, String> headers, RestTemplate restTemplate) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            for (String s : headers.keySet()) {
                httpHeaders.add(s, headers.get(s));
            }
            HttpEntity httpEntity = new HttpEntity<>(null, httpHeaders);
            if (ParamUtil.paramIsNotEmpty(params)) {
                httpEntity = new HttpEntity<>(params, httpHeaders);
            }
            return restTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, String.class).getBody();
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }


    static String post(String requestUrl, MultiValueMap<String, String> params, Map<String, String> headers, String proxyIp, String proxyPort, RestTemplate restTemplate) {
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
                return restTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, String.class).getBody();
            } else {
                if (ParamUtil.paramIsNotEmpty(params)) {
                    return restTemplate.postForEntity(requestUrl, params, String.class).getBody();
                } else {
                    return restTemplate.postForEntity(requestUrl, HttpMethod.POST, String.class).getBody();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpException(e.getCause().getMessage());
        }
    }


    static String get(String requestUrl, RestTemplate restTemplate) {
        try {
            return restTemplate.getForObject(requestUrl, String.class);
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }


    static String get(String requestUrl, Map<String, String> params, RestTemplate restTemplate) {
        try {
            return restTemplate.getForObject(UrlUtil.urlAppend(requestUrl, params), String.class, params);
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }

    static String get(String requestUrl, Map<String, String> params, Map<String, String> headers, RestTemplate restTemplate) {
        try {
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


    static String get(String requestUrl, Map<String, String> params, @ExcludeParam Map<String, String> headers, String proxyIp, String proxyPort, RestTemplate restTemplate) {
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
}
