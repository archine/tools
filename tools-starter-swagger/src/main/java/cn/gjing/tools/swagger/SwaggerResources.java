package cn.gjing.tools.swagger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Gjing
 **/
@Data
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("tools.swagger.resources")
public class SwaggerResources {

    /**
     * 是否开启Swagger资源列表
     */
    @Builder.Default
    private boolean enable = false;
    /**
     * 是否注册本服务,默认true
     */
    @Builder.Default
    private boolean registerMe = true;
    /**
     * 服务列表
     */
    @Builder.Default
    private List<Map<String, SwaggerService>> serviceList = new ArrayList<>();

}

