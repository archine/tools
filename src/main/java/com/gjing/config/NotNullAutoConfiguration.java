package com.gjing.config;

import com.gjing.ex.GlobalExceptionHandler;
import com.gjing.handle.NotNullProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Archine
 **/
@Configuration
@ConditionalOnWebApplication
public class NotNullAutoConfiguration {

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
