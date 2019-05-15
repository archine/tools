package cn.gjing.doc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Gjing
 **/
@Component
@ConfigurationProperties("cn.gjing.swagger-doc")
@EnableConfigurationProperties
@Data
class SwaggerDoc {

    /**
     * 是否注册本服务,默认true
     */
    private boolean registerMe = true;
    /**
     * 目标swagger地址集合
     */
    private List<Map<String,SwaggerDoc.Detail>> docList = new ArrayList<>();

    @Data
    static class Detail{

        /**
         * 目标swagger地址
         */
        private String location="";

        /**
         * 目标swagger版本号
         */
        private String version="1.0";
    }
}
