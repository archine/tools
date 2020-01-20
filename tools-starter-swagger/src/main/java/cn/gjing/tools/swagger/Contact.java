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
@ConfigurationProperties("swagger.contact")
public class Contact {

    @Builder.Default
    private String name = "";
    @Builder.Default
    private String url = "";
    @Builder.Default
    private String email = "";
}
