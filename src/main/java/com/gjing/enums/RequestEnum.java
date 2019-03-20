package com.gjing.enums;

import lombok.Getter;

/**
 * @author Archine
 **/
@Getter
public enum RequestEnum {
    /**
     * 请求方式
     */
    HTTP("http"),
    HTTPS("https");
    private String type;

    RequestEnum(String type) {
        this.type = type;
    }}
