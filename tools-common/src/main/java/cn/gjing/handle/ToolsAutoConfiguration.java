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
    @ConditionalOnClass(NotNull2Processor.class)
    public NotNull2Processor notNullProxy() {
        return new NotNull2Processor();
    }

    @Bean
    public CommonExceptionHandler commonExceptionHandler() {
        return new CommonExceptionHandler();
    }

}
