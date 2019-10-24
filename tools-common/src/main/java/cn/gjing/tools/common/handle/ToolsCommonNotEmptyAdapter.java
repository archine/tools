package cn.gjing.tools.common.handle;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
public class ToolsCommonNotEmptyAdapter {

    @Bean
    @ConditionalOnClass(NotEmptyProcessor.class)
    public NotEmptyProcessor notEmptyProcessor() {
        return new NotEmptyProcessor();
    }
}
