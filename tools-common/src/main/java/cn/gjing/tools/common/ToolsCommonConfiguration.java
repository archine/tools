package cn.gjing.tools.common;

import cn.gjing.tools.common.util.SpringBeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
public class ToolsCommonConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public SpringBeanUtils springBeanUtils() {
        return new SpringBeanUtils();
    }
}
