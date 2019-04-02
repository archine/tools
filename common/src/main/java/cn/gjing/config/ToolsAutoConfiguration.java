package cn.gjing.config;

import cn.gjing.ex.GlobalExceptionHandler;
import cn.gjing.handle.NotNullProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
@ConditionalOnWebApplication
public class ToolsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(NotNullProcessor.class)
    public NotNullProcessor notNullProxy() {
        return new NotNullProcessor();
    }

    @Bean
    @ConditionalOnMissingBean(GlobalExceptionHandler.class)
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

}
