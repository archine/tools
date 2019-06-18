package cn.gjing.handle;

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
public class CorsBean {

    /**
     * 允许的域名
     */
    private String[] allowedOrigins = new String[]{"*"};

    /**
     * 允许的方法
     */
    private String[] allowedMethods = new String[]{"GET", "DELETE", "POST", "PUT","OPTIONS"};

    /**
     * 允许的请求头
     */
    private String[] allowedHeaders = new String[]{"*"};

    /**
     * 是否允许用户发送、处理 cookie
     */
    private Boolean allowCredentials = true;

    /**
     * 预检请求的有效期，单位为秒。有效期内，不会重复发送预检请求
     */
    private Long maxAge = 1800L;

    /**
     * 允许方法路径
     */
    private String path = "/**";
}
