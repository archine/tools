package cn.gjing.tools.common.valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Gjing
 **/
@Configuration
@Slf4j
public class ToolsParamValidationConfigurer implements WebMvcConfigurer {
    private ToolsParamValidationHandle toolsParamValidationHandle;
    private ValidMeta meta;

    public ToolsParamValidationConfigurer(ToolsParamValidationHandle toolsParamValidationHandle, ValidMeta validMeta) {
        this.toolsParamValidationHandle = toolsParamValidationHandle;
        this.meta = validMeta;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(toolsParamValidationHandle)
                .addPathPatterns(this.meta.getPath())
                .excludePathPatterns(this.meta.getExcludePath());
        log.info("Tools common parameter validation processor was initialized successfully…");
    }
}
