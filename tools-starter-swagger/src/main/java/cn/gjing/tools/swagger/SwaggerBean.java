package cn.gjing.tools.swagger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gjing
 **/
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("swagger")
public class SwaggerBean {
    /**
     * 要扫描的包路径(controller包路径)
     */
    @Builder.Default
    private String basePackage = "";

    /**
     * 接口选择规则, ALL (默认,所有接口), ANT (符合指定路径的接口)，REGEX（符合正则表达式的接口）
     */
    @Builder.Default
    private PathType pathType = PathType.ALL;

    /**
     * 接口选则表达式，如果是路径选择，则如：/test/** 即选择test路径下的所有接口，正则选择为满足正则表达式的所有接口
     */
    @Builder.Default
    private String pathPattern="/**";

    /**
     * 排除路径，默认使用正则表达式方式，可在pathType设置为其他类型（pathType类型为ALL时默认走正则）
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

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public PathType getPathType() {
        return pathType;
    }

    public void setPathType(PathType pathType) {
        this.pathType = pathType;
    }

    public String getPathPattern() {
        return pathPattern;
    }

    public void setPathPattern(String pathPattern) {
        this.pathPattern = pathPattern;
    }

    public String[] getExcludePattern() {
        return excludePattern;
    }

    public void setExcludePattern(String[] excludePattern) {
        this.excludePattern = excludePattern;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
        this.termsOfServiceUrl = termsOfServiceUrl;
    }

    public List<ResponseSchema> getGlobalResponseSchemas() {
        return globalResponseSchemas;
    }

    public void setGlobalResponseSchemas(List<ResponseSchema> globalResponseSchemas) {
        this.globalResponseSchemas = globalResponseSchemas;
    }

    public List<RequestHeader> getGlobalHeaders() {
        return globalHeaders;
    }

    public void setGlobalHeaders(List<RequestHeader> globalHeaders) {
        this.globalHeaders = globalHeaders;
    }
}


