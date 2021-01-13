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
     * The contact
     */
    @Builder.Default
    private String name = "";
    /**
     * Contact Website
     */
    @Builder.Default
    private String url = "";
    /**
     * Contact email
     */
    @Builder.Default
    private String email = "";
}
