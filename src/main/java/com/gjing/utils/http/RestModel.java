package com.gjing.utils.http;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.MultiValueMap;

/**
 * @author Archine
 **/
@Data
@NoArgsConstructor
public class RestModel {
    /**
     * 请求url
     */
    private String requestUrl;
    /**
     * 请求参数
     */
    private MultiValueMap<String, Object> params;

    /**
     * 请求头信息
     */
    private MultiValueMap<String, Object> headers;

    /**
     * 代理ip
     */
    private String proxyIp;
    /**
     * 代理端口
     */
    private String proxyPort;

}
