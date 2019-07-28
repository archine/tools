package cn.gjing.handle;

import cn.gjing.BeanUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
public class ToolsCommonAdapter {

    @Bean
    @ConditionalOnClass(NotNull2Processor.class)
    public NotNull2Processor notNullProxy() {
        return new NotNull2Processor();
    }

    @Bean
    public BeanUtil commonBeanUtil() {
        return new BeanUtil();
    }
}
