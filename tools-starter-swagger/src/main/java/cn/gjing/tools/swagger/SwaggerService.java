package cn.gjing.tools.swagger;

import lombok.*;

/**
 * @author Gjing
 **/
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SwaggerService {
    /**
     * 文档标签名
     */
    private String view;

    /**
     * 目标服务名或者文档地址
     */
    private String service;
}
