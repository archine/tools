package cn.gjing.tools.swagger.core;

import cn.gjing.tools.swagger.DocGroup;
import cn.gjing.tools.swagger.DocGroupHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * @author Gjing
 **/
class GroupDocConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "tools.doc.group.enable",havingValue = "true")
    public DocGroup swaggerResources() {
        return new DocGroup();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "tools.doc.group.enable",havingValue = "true")
    public DocGroupHandler swaggerResourceHandler() {
        return new DefaultNameGroupDocHandler();
    }
}
