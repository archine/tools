package cn.gjing.tools.common.valid;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Gjing
 **/
@Configuration
class ParamValidConfigurer implements WebMvcConfigurer {
    private ParamValidationHandle paramValidationHandle;
    private ValidMeta meta;

    public ParamValidConfigurer(ParamValidationHandle paramValidationHandle, ValidMeta validMeta) {
        this.paramValidationHandle = paramValidationHandle;
        this.meta = validMeta;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(paramValidationHandle)
                .addPathPatterns(this.meta.getPath())
                .excludePathPatterns(this.meta.getExcludePath());
    }
}
