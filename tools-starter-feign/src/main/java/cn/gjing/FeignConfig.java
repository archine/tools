package cn.gjing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
public class FeignConfig {

    @Bean
    public FeignProcess feignProcess() {
        return new FeignProcess();
    }

    @Bean
    public BeanUtil beanUtil() {
        return new BeanUtil();
    }

}
