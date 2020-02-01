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
@ConfigurationProperties(prefix = "aliyun")
public class AliyunMeta {
    /**
     * 用户key
     */
    private String accessKey;

    /**
     * 用户秘钥
     */
    private String accessKeySecret;
}
