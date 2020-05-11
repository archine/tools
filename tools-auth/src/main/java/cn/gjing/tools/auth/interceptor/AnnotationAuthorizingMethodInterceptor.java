package cn.gjing.tools.auth.interceptor;

import cn.gjing.tools.auth.AuthorizationInfo;
import cn.gjing.tools.auth.AuthorizationListener;
import cn.gjing.tools.auth.annotation.RequiredPermissions;
import cn.gjing.tools.auth.annotation.RequiredRoles;
import cn.gjing.tools.auth.exception.NoAccountException;
import cn.gjing.tools.auth.handler.AnnotationHandler;
import cn.gjing.tools.auth.metadata.AuthorizationMetaData;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Permission annotation handler to authenticate a method with the {@link RequiredRoles}
 * and {@link RequiredPermissions} annotation
 *
 * @author Gjing
 **/
@Component
public class AnnotationAuthorizingMethodInterceptor implements AuthorizingMethodInterceptor {
    private final List<AnnotationHandler> methodInterceptors;
    private final AuthorizationListener authorizationListener;
    private final AuthorizationInfo authorizationInfo;

    public AnnotationAuthorizingMethodInterceptor(List<AnnotationHandler> methodInterceptors, AuthorizationListener authorizationListener,
                                                  AuthorizationInfo authorizationInfo) {
        this.methodInterceptors = methodInterceptors;
        this.authorizationListener = authorizationListener;
        this.authorizationInfo = authorizationInfo;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            String token = request.getHeader(this.authorizationInfo.getHeader());
            AuthorizationMetaData authorizationMetaData = this.authorizationListener.supplyAccess(token);
            if (authorizationMetaData == null) {
                throw new NoAccountException("Account not found");
            }
            if (this.methodInterceptors != null) {
                for (AnnotationHandler annotationHandler : methodInterceptors) {
                    Annotation annotation = method.getAnnotation(annotationHandler.getAnnotationClass());
                    if (annotation != null) {
                        annotationHandler.assertAuthorization(annotation, authorizationMetaData);
                    }
                }
            }
            this.authorizationListener.authentication(token);
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) throws Exception {
        this.authorizationListener.authenticationSuccess(request, ((HandlerMethod) handler).getMethod());
    }
}
