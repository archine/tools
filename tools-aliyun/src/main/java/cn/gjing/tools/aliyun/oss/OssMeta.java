package cn.gjing.tools.aliyun.oss;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Gjing
 **/
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "tools.aliyun.oss")
public class OssMeta {
    /**
     * Ali cloud user accesskey, Priority is greater than global configuration
     */
    private String accessKey;

    /**
     * Ali cloud user access key secret, Priority is greater than global configuration
     */
    private String accessKeySecret;

    /**
     * End point
     */
    private String endPoint;

    /**
     * Bucket
     */
    private String bucket;

    /**
     * Max connections
     */
    private Integer maxConnections = 1024;

    /**
     * Socket timeout
     */
    private Integer socketTimeout = 50000;

    /**
     * Idle time
     */
    private Integer idleTime = 60000;

    /**
     * Connection timeout
     */
    private Integer connectionTimeout = 50000;
}
