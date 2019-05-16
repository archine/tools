package cn.gjing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Gjing
 **/
@Component
@ConfigurationProperties("swagger")
@EnableConfigurationProperties
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SwaggerBean {
    /**
     * 要扫描的包路径(controller包路径)
     */
    private String basePackage = "";

    /**
     * 接口选择规则, ALL (默认,所有接口), ANT (符合指定路径的接口)，REGEX（符合正则表达式的接口）
     */
    private PathType pathType = PathType.ALL;

    /**
     * 接口选则表达式，如果是路径选择，则如：/test/** 即选择test路径下的所有接口，正则选择为满足正则表达式的所有接口
     */
    private String pathPattern;

    /**
     * swagger文档标题
     */
    private String title = "Api documentation";
    /**
     * swagger文档描述
     */
    private String description = "";
    /**
     * swagger版本号
     */
    private String version = "2.0";
    /**
     * 许可证
     */
    private String license = "";

    /**
     * 许可证地址
     */
    private String licenseUrl = "";

    /**
     * 服务条款
     */
    private String termsOfServiceUrl = "";
}


