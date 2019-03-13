package com.gj.config;

import com.gj.ex.GlobalExceptionHandler;
import com.gj.proxy.NotNullMethodParamHandler;
import com.gj.proxy.NotNullProxy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Archine
 * @date 2019-03-13
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
    @ConditionalOnMissingBean(NotNullMethodParamHandler.class)
    public NotNullMethodParamHandler notNullMethodParamHandler() {
        return new NotNullMethodParamHandler();
    }

    @Bean
    @ConditionalOnMissingBean(GlobalExceptionHandler.class)
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(MvcConfig.class)
    public MvcConfig mvcConfig() {
        return new MvcConfig();
    }

}
