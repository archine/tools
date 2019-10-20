package cn.gjing.tools.common.handle;

import cn.gjing.tools.common.annotation.Exclude2;
import cn.gjing.tools.common.annotation.NotEmpty;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author Gjing
 * This class is primarily used for method parsing as a notnull annotation to detect whether all parameters on a method
 * contain null values , Throws an {ParamException} if it contains null values
 **/
@Component
@Aspect
class NotEmptyProcessor {
    @Pointcut("@annotation(cn.gjing.tools.common.annotation.NotEmpty)")
    public void cut() {

    }

    @Before("cut()")
    @SuppressWarnings("unchecked")
    public void validation(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        NotEmpty notEmpty = method.getAnnotation(NotEmpty.class);
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(Exclude2.class)) {
                continue;
            }
            if (args[i] == null) {
                throw new NullPointerException("".equals(notEmpty.message())
                        ? "The parameter " + parameter.getName() + " cannot be null"
                        : notEmpty.message());
            }
        }
    }

}
