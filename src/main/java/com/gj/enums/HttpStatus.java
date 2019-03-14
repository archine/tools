package com.gj.enums;

import lombok.Getter;

/**
 * @author Archine
 **/
@Getter
public enum HttpStatus {
    /**
     * http response status
     */
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    URI_TOO_LONG(414, "URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    PARAM_EMPTY(512, "Parameter Is Null"),
    NO_LOGIN(513,"Current Not Login,To Login Please");
    private Integer code;
    private String msg;
    HttpStatus(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
