package cn.gjing.tools.auth.config;

import cn.gjing.tools.auth.AuthorizationInfo;
import cn.gjing.tools.auth.AuthorizationListener;
import cn.gjing.tools.auth.TokenAssistant;
import cn.gjing.tools.auth.handler.AnnotationHandler;
import cn.gjing.tools.auth.handler.PermissionAnnotationHandler;
import cn.gjing.tools.auth.handler.RoleAnnotationHandler;
import cn.gjing.tools.auth.interceptor.AnnotationAuthorizingMethodInterceptor;
import cn.gjing.tools.auth.interceptor.AuthorizingMethodInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Permission dependent configuration
 *
 * @author Gjing
 **/
@Configuration
@SuppressWarnings("all")
class SnowAuthorizationConfiguration {

    /**
     * Authorization info
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthorizationInfo authorizationInfo() {
        return new AuthorizationInfo();
    }

    /**
     * Token generation and parsing assistant
     *
     * @return TokenHelper
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenAssistant tokenHelper() {
        return new TokenAssistant(authorizationInfo());
    }

    /**
     * Permission method interceptor
     *
     * @param authorizationListener AuthorizationListener
     * @return AuthorizingMethodInterceptor
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthorizingMethodInterceptor authorizingMethodHandler(AuthorizationListener authorizationListener, AuthorizationInfo authorizationInfo) {
        List<AnnotationHandler> annotationHandlerList = new ArrayList<>(2);
        annotationHandlerList.add(new RoleAnnotationHandler());
        annotationHandlerList.add(new PermissionAnnotationHandler());
        AuthorizingMethodInterceptor authorizingMethodInterceptor;
        return new AnnotationAuthorizingMethodInterceptor(annotationHandlerList, authorizationListener, authorizationInfo);
    }

    /**
     * Permission method intercepts the processor registry
     *
     * @param authorizationListener AuthorizationListener
     * @return SnowAuthorizingInterceptorRegister
     */
    @Bean
    @ConditionalOnMissingBean
    public SnowAuthorizingInterceptorRegister snowAuthConfigure(AuthorizationListener authorizationListener, AuthorizingMethodInterceptor authorizingMethodInterceptor) {
        return new SnowAuthorizingInterceptorRegister(authorizationInfo(), authorizingMethodInterceptor);
    }
}

