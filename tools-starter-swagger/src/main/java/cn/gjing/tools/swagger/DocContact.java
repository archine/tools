package cn.gjing.tools.swagger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@ConfigurationProperties("tools.doc.contact")
public class DocContact {
    /**
     * 联系人姓名
     */
    @Builder.Default
    private String name = "";
    /**
     * 联系人个人url
     */
    @Builder.Default
    private String url = "";
    /**
     * 联系人邮箱
     */
    @Builder.Default
    private String email = "";
}
