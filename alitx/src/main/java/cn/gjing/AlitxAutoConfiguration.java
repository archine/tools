package cn.gjing;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
public class AlitxAutoConfiguration {

    @Bean
    @ConditionalOnBean(AlitxExceptionHandler.class)
    public AlitxExceptionHandler alitxExceptionHandler() {
        return new AlitxExceptionHandler();
    }
}
