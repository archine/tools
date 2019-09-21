package cn.gjing.tools.common.cors;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ConfigurationProperties("cors")
@EnableConfigurationProperties
public class Cors {

    /**
     * 允许的域名
     */
    @Builder.Default
    private String[] allowedOrigins = new String[]{"*"};

    /**
     * 允许的方法
     */
    @Builder.Default
    private String[] allowedMethods = new String[]{"GET", "DELETE", "POST", "PUT"};

    /**
     * 允许的请求头
     */
    @Builder.Default
    private String[] allowedHeaders = new String[]{"*"};

    /**
     * 是否允许用户发送、处理 cookie
     */
    @Builder.Default
    private Boolean allowCredentials = true;

    /**
     * 预检请求的有效期，单位为秒。有效期内，不会重复发送预检请求
     */
    @Builder.Default
    private Long maxAge = 1800L;

    /**
     * 允许方法路径
     */
    @Builder.Default
    private String path = "/**";
}
