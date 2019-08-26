package cn.gjing.handle;

import cn.gjing.util.BeanUtil;
import cn.gjing.util.id.IdUtil;
import cn.gjing.util.id.SnowId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    @ConditionalOnMissingBean(BeanUtil.class)
    public BeanUtil beanUtil() {
        return new BeanUtil();
    }

    @Bean
    @ConditionalOnMissingBean(SnowId.class)
    public SnowId snowId() {
        return new SnowId();
    }

    @Bean
    @ConditionalOnMissingBean(value = {IdUtil.class})
    @ConditionalOnBean(SnowId.class)
    public IdUtil idUtil() {
        return new IdUtil(snowId());
    }
}
