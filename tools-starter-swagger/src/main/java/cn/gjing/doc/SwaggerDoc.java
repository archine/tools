package cn.gjing.doc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gjing
 **/
@Component
@ConfigurationProperties("cn.gjing.swagger-doc")
@EnableConfigurationProperties
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwaggerDoc {

    /**
     * 是否注册本服务,默认true
     */
    private boolean registerMe = true;
    /**
     * 服务名
     */
    private List<String> serveList = new ArrayList<>();

}

