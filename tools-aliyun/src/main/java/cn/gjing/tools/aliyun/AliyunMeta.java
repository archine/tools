package cn.gjing.tools.aliyun;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Gjing
 **/
@Data
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "tools.aliyun")
public class AliyunMeta {
    /**
     * Ali cloud user accesskey
     */
    private String accessKey;

    /**
     * Ali cloud user access key secret
     */
    private String accessKeySecret;
}
