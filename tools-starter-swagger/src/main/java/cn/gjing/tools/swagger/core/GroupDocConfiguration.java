package cn.gjing.tools.swagger.core;

import cn.gjing.tools.swagger.DocGroup;
import cn.gjing.tools.swagger.DocGroupHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * @author Gjing
 **/
class GroupDocConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public DocGroup swaggerResources() {
        return new DocGroup();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public DocGroupHandler swaggerResourceHandler(DocGroup docGroup) {
        switch (docGroup.getType()) {
            case URL:
                return new DefaultUrlGroupDocHandler();
            case NAME:
                return new DefaultNameGroupDocHandler();
            default:
                throw new IllegalArgumentException("Cannot found your doc handler");
        }
    }
}
