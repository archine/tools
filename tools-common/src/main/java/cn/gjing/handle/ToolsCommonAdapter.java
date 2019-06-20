package cn.gjing.handle;

import cn.gjing.SpringBeanUtil;
import cn.gjing.ex.CommonExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
public class ToolsCommonAdapter {

    @Bean
    public NotNull2Processor notNullProxy() {
        return new NotNull2Processor();
    }

    @Bean
    public CommonExceptionHandler commonExceptionHandler() {
        return new CommonExceptionHandler();
    }

    @Bean
    public SpringBeanUtil springBeanUtil() {
        return new SpringBeanUtil();
    }
}
