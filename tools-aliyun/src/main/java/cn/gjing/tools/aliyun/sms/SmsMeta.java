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
