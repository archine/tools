package cn.gjing.tools.auth.config;

import cn.gjing.tools.auth.AuthorizationInfo;
import cn.gjing.tools.auth.interceptor.AuthorizingMethodInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The permission method intercepts the processor registry
 *
 * @author Gjing
 **/
@Configuration
class SnowAuthorizingInterceptorRegister implements WebMvcConfigurer {
    private final AuthorizationInfo authorizationInfo;
    private final AuthorizingMethodInterceptor authorizingMethodInterceptor;

    public SnowAuthorizingInterceptorRegister(AuthorizationInfo authorizationInfo, AuthorizingMethodInterceptor authorizingMethodInterceptor) {
        this.authorizationInfo = authorizationInfo;
        this.authorizingMethodInterceptor = authorizingMethodInterceptor;
    }

    /**
     * Register permission interceptors
     *
     * @param registry InterceptorRegistry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizingMethodInterceptor)
                .addPathPatterns(this.authorizationInfo.getPath())
                .excludePathPatterns(this.authorizationInfo.getFilter());
    }
}
