package cn.gjing.tools.common.valid;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "valid")
public class ValidMeta {
    /**
     * 校验接口路径
     */
    private String[] path = {"/**"};

    /**
     * 排除接口路径
     */
    private String[] excludePath = {"/error", "/swagger-resources/**", "/v2/**", "/webjars/**"};
}
