package cn.gjing.tools.common.valid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gjing
 **/
@Configuration
class ToolsParamValidationAdapter {
    @Bean
    @ConditionalOnMissingBean
    public ToolsParamValidationHandle toolsParamValidationHandle() {
        return new ToolsParamValidationHandle();
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidMeta validMeta() {
        return new ValidMeta();
    }

    @Bean
    @ConditionalOnMissingBean
    public ToolsParamValidationFilter toolsParamValidationFilter() {
        return new ToolsParamValidationFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ToolsParamValidationConfigurer toolsParamValidationConfigurer() {
        return new ToolsParamValidationConfigurer(toolsParamValidationHandle(),validMeta());
    }
}
