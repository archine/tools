package cn.gjing.config;

import cn.gjing.ex.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
public class AlitxAutoConfiguration {

    @Bean
    @ConditionalOnBean(GlobalExceptionHandler.class)
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
