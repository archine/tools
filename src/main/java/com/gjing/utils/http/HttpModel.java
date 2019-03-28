package com.gjing.utils.http;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author Archine
 **/
@Data
@NoArgsConstructor
public class HttpModel {
    /**
     * 请求url
     */
    private String requestUrl;
    /**
     * 请求参数
     */
    private Map<String, String> params;

    /**
     * 请求头信息
     */
    private Map<String, String> headers;

    /**
     * 代理ip
     */
    private String proxyIp;
    /**
     * 代理端口
     */
    private String proxyPort;

}
