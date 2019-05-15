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
@ConfigurationProperties("cn.gjing.swagger")
@EnableConfigurationProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SwaggerBean {
    /**
     * 要扫描的包路径(controller包路径)
     */
    private String basePackage="";
    /**
     * swagger文档标题
     */
    private String title="Api documentation";
    /**
     * swagger文档描述
     */
    private String description="";
    /**
     * swagger版本号
     */
    private String version="1.0";

}
