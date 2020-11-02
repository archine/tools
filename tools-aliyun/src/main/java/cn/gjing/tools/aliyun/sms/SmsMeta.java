package cn.gjing.tools.aliyun.sms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * SMS meta data
 *
 * @author Gjing
 **/
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "tools.aliyun.sms")
public class SmsMeta {
    /**
     * Ali cloud user accesskey, Priority is greater than global configuration
     */
    private String accessKey;

    /**
     * Ali cloud user access key secret, Priority is greater than global configuration
     */
    private String accessKeySecret;

    /**
     * SMS sign name
     */
    private String signName;

    /**
     * SMS template code
     */
    private String templateCode;

    /**
     * region
     */
    private String region = "default";
}
