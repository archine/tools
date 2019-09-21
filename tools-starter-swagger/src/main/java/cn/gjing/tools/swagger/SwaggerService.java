package cn.gjing.tools.swagger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gjing
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwaggerService {

    /**
     * 目标文档展示的提示名
     */
    private String view;

    /**
     * 目标服务名
     */
    private String service;
}
