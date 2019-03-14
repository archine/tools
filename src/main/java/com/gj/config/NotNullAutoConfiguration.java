package com.gj.config;

import com.gj.ex.GlobalExceptionHandler;
import com.gj.proxy.NotNullProxy;
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
    @ConditionalOnMissingBean(NotNullProxy.class)
    public NotNullProxy notNullProxy() {
        return new NotNullProxy();
    }


    @Bean
    @ConditionalOnMissingBean(GlobalExceptionHandler.class)
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

}
