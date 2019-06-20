package cn.gjing;

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
@Component
@ConfigurationProperties("swagger.resources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resources {

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
     * 服务名和地址以及列表展示名
     */
    @Builder.Default
    private List<Map<String,Serve>> serveList = new ArrayList<>();

}

