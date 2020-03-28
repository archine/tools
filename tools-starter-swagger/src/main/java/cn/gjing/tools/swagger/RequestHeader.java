package cn.gjing.tools.swagger;

import lombok.*;

/**
 * 请求头
 *
 * @author Gjing
 **/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestHeader {
    /**
     * 请求头名称
     */
    private String name;
    /**
     * 描述
     */
    private String desc;
    /**
     * 是否必填, 默认true
     */
    private boolean required = false;
}
