package cn.gjing.swagger;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SwaggerBean {
    /**
     * 要扫描的包路径(controller包路径)
     */
    private String basePackage = "";

    /**
     * 接口选择规则,ALL(所有),NONE(不选择),ANT(路径选择)，REGEX（正则选择）
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
    private String version = "1.0";

    /**
     * 联系方式
     */
    private Contact contact = new Contact();
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
