package com.gjing.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Archine
 **/
@Getter
@NoArgsConstructor
public enum HttpType {
    /**
     * 请求方式
     */
    HTTP("http"),
    HTTPS("https");
    private String type;

    HttpType(String type) {
        this.type = type;
    }
}
