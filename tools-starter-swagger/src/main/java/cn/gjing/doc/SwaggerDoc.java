package cn.gjing.doc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Gjing
 **/
@Component
@ConfigurationProperties("cn.gjing.swagger.doc")
@EnableConfigurationProperties
@Data
class SwaggerDoc {

    /**
     * 目标swagger地址集合
     */
    private List<Map<String,SwaggerDoc.detail>> docList;

    @Data
    static class detail{

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
