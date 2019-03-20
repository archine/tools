package com.gjing.utils.http;

import com.gjing.config.HttpsClientRequestFactory;
import com.gjing.enums.HttpStatus;
import com.gjing.enums.RequestEnum;
import com.gjing.ex.HttpException;
import com.gjing.utils.Gj;
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
     * @param restModel request entity
     * @return request result
     */
    public String get(RestModel restModel) {
        if (!Gj.multiParamHasEmpty(Arrays.asList(restModel.getProxyIp(), restModel.getProxyPort()))) {
            setProxy(restModel.getProxyIp(), restModel.getProxyPort());
        }
        try {
            checkRequestType(restModel);
            if (Gj.paramIsNotEmpty(restModel.getHeaders())) {
                HttpHeaders httpHeaders = new HttpHeaders();
                for (String s : restModel.getHeaders().keySet()) {
                    httpHeaders.add(s, restModel.getHeaders().get(s).toString());
                }
                HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
                if (Gj.paramIsNotEmpty(restModel.getParams())) {
                    return restTemplate.exchange(Gj.urlAppend(restModel.getRequestUrl(), restModel.getParams().toSingleValueMap()), HttpMethod.GET, httpEntity, String.class, restModel.getParams().toSingleValueMap()).getBody();
                } else {
                    return restTemplate.exchange(restModel.getRequestUrl(), HttpMethod.GET, httpEntity, String.class).getBody();
                }
            } else {
                if (Gj.paramIsNotEmpty(restModel.getParams())) {
                    return restTemplate.getForObject(Gj.urlAppend(restModel.getRequestUrl(), restModel.getParams().toSingleValueMap()), String.class, restModel.getParams().toSingleValueMap());
                } else {
                    return restTemplate.getForObject(restModel.getRequestUrl(), String.class);
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
     * @param restModel request entity
     * @return request result
     */
    public String post(RestModel restModel) {
        if (!Gj.multiParamHasEmpty(Arrays.asList(restModel.getProxyIp(), restModel.getProxyPort()))) {
            setProxy(restModel.getProxyIp(), restModel.getProxyPort());
        }
        try {
            checkRequestType(restModel);
            HttpEntity<Object> httpEntity;
            if (Gj.paramIsNotEmpty(restModel.getHeaders())) {
                HttpHeaders httpHeaders = new HttpHeaders();
                for (String s : httpHeaders.keySet()) {
                    httpHeaders.add(s, restModel.getHeaders().get(s).toString());
                }
                httpEntity = new HttpEntity<>(null, httpHeaders);
                if (Gj.paramIsNotEmpty(restModel.getParams())) {
                    httpEntity = new HttpEntity<>(restModel.getParams(), httpHeaders);
                }
                return restTemplate.exchange(restModel.getRequestUrl(), HttpMethod.POST, httpEntity, String.class).getBody();
            } else {
                if (Gj.paramIsNotEmpty(restModel.getParams())) {
                    return restTemplate.postForEntity(restModel.getRequestUrl(), restModel.getParams(), String.class).getBody();
                } else {
                    return restTemplate.postForEntity(restModel.getRequestUrl(), HttpMethod.POST, String.class).getBody();
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
     * @param restModel rest
     */
    private void checkRequestType(RestModel restModel) {
        String[] urlArr = Gj.split(restModel.getRequestUrl(), ":");
        if (Gj.paramIsNotEmpty(urlArr)) {
            if (Objects.equals(Gj.toLowerCase(urlArr[0]), RequestEnum.HTTP.getType())) {
                restTemplate = new RestTemplate();
            } else if (Objects.equals(Gj.toLowerCase(urlArr[0]), RequestEnum.HTTPS.getType())) {
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
