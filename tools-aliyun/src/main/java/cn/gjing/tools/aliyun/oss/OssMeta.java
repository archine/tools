package cn.gjing.tools.aliyun.oss;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Gjing
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "oss")
public class OssMeta {
    /**
     * 节点
     */
    private String endPoint;

    /**
     * 存储空间
     */
    private String bucket;

    /**
     * 用户key
     */
    private String accessKey;

    /**
     * 用户秘钥
     */
    private String accessKeySecret;

    /**
     * 最大连接数,默认1024
     */
    @Builder.Default
    private int maxConnections = 1024;

    /**
     * Socket层传输数据的超时时间,默认50000ms
     */
    @Builder.Default
    private int socketTimeout = 50000;

    /**
     * 空闲超时时间,默认60000ms
     */
    @Builder.Default
    private int idleTime = 60000;

    /**
     * 连接超时时间,默认50000ms
     */
    @Builder.Default
    private int connectionTimeout = 50000;
}
