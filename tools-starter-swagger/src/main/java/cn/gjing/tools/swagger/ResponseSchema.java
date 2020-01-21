package cn.gjing.tools.swagger;

import lombok.*;

/**
 * 全局响应
 *
 * @author Gjing
 **/
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSchema {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 信息
     */
    private String message;
    /**
     * 结果Bean的名称
     */
    private String schema;
}
