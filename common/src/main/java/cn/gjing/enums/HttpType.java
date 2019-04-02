package cn.gjing.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Gjing
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
