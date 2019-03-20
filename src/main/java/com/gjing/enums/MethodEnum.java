package com.gjing.enums;

import lombok.Getter;

/**
 * @author Archine
 **/
@Getter
public enum MethodEnum {
    /**
     * 请求方式
     */
    GET("GET"),POST("POST");

    private String method;

    MethodEnum(String method) {
        this.method = method;
    }}
