package cn.gjing.tools.common.handle;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
public class ToolsCommonNotEmptyAdapter {

    @Bean
    @ConditionalOnMissingBean(NotEmptyProcessor.class)
    public NotEmptyProcessor notEmptyProcessor() {
        return new NotEmptyProcessor();
    }
}
