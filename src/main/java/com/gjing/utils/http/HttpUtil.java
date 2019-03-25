package com.gjing.utils.http;

import com.gjing.config.HttpsClientRequestFactory;
import com.gjing.enums.HttpStatus;
import com.gjing.enums.RequestEnum;
import com.gjing.ex.HttpException;
import com.gjing.utils.ParamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Archine
 **/
@Slf4j
public class HttpUtil {
    private RestTemplate restTemplate;

    /**
     * GET
     *
     * @param httpModel request entity
     * @return request result
     */
    public String get(HttpModel httpModel) {
        if (!ParamUtil.multiParamHasEmpty(Arrays.asList(httpModel.getProxyIp(), httpModel.getProxyPort()))) {
            setProxy(httpModel.getProxyIp(), httpModel.getProxyPort());
        }
        try {
            checkRequestType(httpModel);
            if (ParamUtil.paramIsNotEmpty(httpModel.getHeaders())) {
                HttpHeaders httpHeaders = new HttpHeaders();
                for (String s : httpModel.getHeaders().keySet()) {
                    httpHeaders.add(s, httpModel.getHeaders().get(s).toString());
                }
                HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
                if (ParamUtil.paramIsNotEmpty(httpModel.getParams())) {
                    return restTemplate.exchange(UrlUtil.urlAppend(httpModel.getRequestUrl(), httpModel.getParams().toSingleValueMap()), HttpMethod.GET, httpEntity, String.class, httpModel.getParams().toSingleValueMap()).getBody();
                } else {
                    return restTemplate.exchange(httpModel.getRequestUrl(), HttpMethod.GET, httpEntity, String.class).getBody();
                }
            } else {
                if (ParamUtil.paramIsNotEmpty(httpModel.getParams())) {
                    return restTemplate.getForObject(UrlUtil.urlAppend(httpModel.getRequestUrl(), httpModel.getParams().toSingleValueMap()), String.class, httpModel.getParams().toSingleValueMap());
                } else {
                    return restTemplate.getForObject(httpModel.getRequestUrl(), String.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpException(HttpStatus.BAD_REQUEST.getMsg());
        }
    }

    /**
     * POST
     *
     * @param httpModel request entity
     * @return request result
     */
    public String post(HttpModel httpModel) {
        if (!ParamUtil.multiParamHasEmpty(Arrays.asList(httpModel.getProxyIp(), httpModel.getProxyPort()))) {
            setProxy(httpModel.getProxyIp(), httpModel.getProxyPort());
        }
        try {
            checkRequestType(httpModel);
            HttpEntity<Object> httpEntity;
            if (ParamUtil.paramIsNotEmpty(httpModel.getHeaders())) {
                HttpHeaders httpHeaders = new HttpHeaders();
                for (String s : httpModel.getHeaders().keySet()) {
                    httpHeaders.add(s, httpModel.getHeaders().get(s).toString());
                }
                httpEntity = new HttpEntity<>(null, httpHeaders);
                if (ParamUtil.paramIsNotEmpty(httpModel.getParams())) {
                    httpEntity = new HttpEntity<>(httpModel.getParams(), httpHeaders);
                }
                return restTemplate.exchange(httpModel.getRequestUrl(), HttpMethod.POST, httpEntity, String.class).getBody();
            } else {
                if (ParamUtil.paramIsNotEmpty(httpModel.getParams())) {
                    return restTemplate.postForEntity(httpModel.getRequestUrl(), httpModel.getParams(), String.class).getBody();
                } else {
                    return restTemplate.postForEntity(httpModel.getRequestUrl(), HttpMethod.POST, String.class).getBody();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpException(e.getCause().getMessage());
        }
    }

    /**
     * check request type for http or https
     *
     * @param httpModel rest
     */
    private void checkRequestType(HttpModel httpModel) {
        String[] urlArr = ParamUtil.split(httpModel.getRequestUrl(), ":");
        if (ParamUtil.paramIsNotEmpty(urlArr)) {
            if (Objects.equals(ParamUtil.toLowerCase(urlArr[0]), RequestEnum.HTTP.getType())) {
                restTemplate = new RestTemplate();
            } else if (Objects.equals(ParamUtil.toLowerCase(urlArr[0]), RequestEnum.HTTPS.getType())) {
                restTemplate = new RestTemplate(new HttpsClientRequestFactory());
            } else {
                throw new HttpException("The requested url is malformed");
            }
        }
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
