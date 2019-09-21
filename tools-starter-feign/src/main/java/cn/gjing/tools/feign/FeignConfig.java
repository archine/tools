package cn.gjing.tools.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
class FeignConfig {

    @Bean
    public FeignProcess feignProcess() {
        return new FeignProcess();
    }

    @Bean
    public BeanUtil beanUtil() {
        return new BeanUtil();
    }

}
