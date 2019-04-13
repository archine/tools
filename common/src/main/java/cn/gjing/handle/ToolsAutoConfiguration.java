package cn.gjing.handle;

import cn.gjing.ex.CommonExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
public class ToolsAutoConfiguration {

    @Bean
    @ConditionalOnClass(NotNullProcessor.class)
    public NotNullProcessor notNullProxy() {
        return new NotNullProcessor();
    }

    @Bean
    @ConditionalOnClass(CommonExceptionHandler.class)
    public CommonExceptionHandler commonExceptionHandler() {
        return new CommonExceptionHandler();
    }

}
