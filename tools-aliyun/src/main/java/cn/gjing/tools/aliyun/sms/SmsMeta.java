package cn.gjing.tools.aliyun.sms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Gjing
 **/
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "tools.aliyun.sms")
public class SmsMeta {
    /**
     * 短信签名
     */
    private String signName;

    /**
     * 短信模板Id
     */
    private String templateCode;

    /**
     * 节点
     */
    private String region = "default";
}
