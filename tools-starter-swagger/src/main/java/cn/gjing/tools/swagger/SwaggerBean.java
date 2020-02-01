package cn.gjing.tools.swagger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gjing
 **/
@Data
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("swagger")
public class SwaggerBean {
    /**
     * 开启Swagger文档
     */
    @Builder.Default
    private boolean enable = true;

    /**
     * 要扫描的包路径(controller包路径)
     */
    @Builder.Default
    private String basePackage = "";

    /**
     * 接口选择规则, ANT (指定路径)，REGEX（正则表达式)
     */
    @Builder.Default
    private PathType pathType = PathType.ANT;

    /**
     * 接口选则表达式
     */
    @Builder.Default
    private String pathPattern="/**";

    /**
     * 接口排除路径表达式
     */
    @Builder.Default
    private String[] excludePattern = new String[]{};

    /**
     * swagger文档标题
     */
    @Builder.Default
    private String title = "Api documentation";
    /**
     * swagger文档描述
     */
    @Builder.Default
    private String description = "";
    /**
     * swagger版本号
     */
    @Builder.Default
    private String version = "2.0";
    /**
     * 许可证
     */
    @Builder.Default
    private String license = "";

    /**
     * 许可证地址
     */
    @Builder.Default
    private String licenseUrl = "";

    /**
     * 服务条款
     */
    @Builder.Default
    private String termsOfServiceUrl = "";

    /**
     * 全局响应信息
     */
    @Builder.Default
    private List<ResponseSchema> globalResponseSchemas = new ArrayList<>();

    /**
     * 请求头
     */
    @Builder.Default
    private List<RequestHeader> globalHeaders = new ArrayList<>();
}


