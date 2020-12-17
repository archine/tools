package cn.gjing.tools.aliyun.oss;

import cn.gjing.tools.aliyun.AliyunMeta;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Gjing
 **/
@ToString
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "tools.aliyun.oss")
public final class OssMeta {
    /**
     * Ali cloud user access key, Priority is greater than global configuration
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

    private OSS ossClient;

    public OSS getOssClient(AliyunMeta aliyunMeta) {
        if (this.ossClient == null) {
            ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
            conf.setMaxConnections(this.getMaxConnections());
            conf.setSocketTimeout(this.getSocketTimeout());
            conf.setConnectionTimeout(this.getConnectionTimeout());
            conf.setIdleConnectionTime(this.getIdleTime());
            this.ossClient = new OSSClientBuilder().build(this.getEndPoint(), StringUtils.isEmpty(this.getAccessKey()) ? aliyunMeta.getAccessKey() : this.getAccessKey(),
                    StringUtils.isEmpty(this.getAccessKeySecret()) ? aliyunMeta.getAccessKeySecret() : this.getAccessKeySecret(), conf);
        }
        return ossClient;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public String getBucket() {
        return bucket;
    }

    public Integer getMaxConnections() {
        return maxConnections;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public Integer getIdleTime() {
        return idleTime;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setIdleTime(Integer idleTime) {
        this.idleTime = idleTime;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
}
