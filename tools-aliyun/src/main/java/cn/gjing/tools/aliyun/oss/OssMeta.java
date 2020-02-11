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
     * 节点
     */
    private String endPoint;

    /**
     * 存储空间
     */
    private String bucket;

    /**
     * 最大连接数
     */
    private Integer maxConnections = 1024;

    /**
     * Socket层传输数据的超时时间
     */
    private Integer socketTimeout = 50000;

    /**
     * 空闲超时时间
     */
    private Integer idleTime = 60000;

    /**
     * 连接超时时间
     */
    private Integer connectionTimeout = 50000;
}
