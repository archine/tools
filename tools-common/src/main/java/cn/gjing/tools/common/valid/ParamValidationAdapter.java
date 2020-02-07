package cn.gjing.tools.common.valid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
class ParamValidationAdapter {
    @Bean
    @ConditionalOnMissingBean(ParamValidationHandle.class)
    public ParamValidationHandle paramValidationHandle() {
        return new ParamValidationHandle();
    }

    @Bean
    @ConditionalOnMissingBean(ValidMeta.class)
    public ValidMeta validMeta() {
        return new ValidMeta();
    }

    @Bean
    public ParamValidationRequestFilter requestFilter() {
        return new ParamValidationRequestFilter();
    }

    @Bean
    @ConditionalOnMissingBean(ParamValidConfigurer.class)
    public ParamValidConfigurer paramValidConfigurer() {
        return new ParamValidConfigurer(paramValidationHandle(),validMeta());
    }
}
