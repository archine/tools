package cn.gjing.tools.common.handle;

import cn.gjing.tools.common.annotation.*;
import cn.gjing.tools.common.exception.ParamValidException;
import cn.gjing.tools.common.util.BeanUtils;
import cn.gjing.tools.common.util.ParamUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author Gjing
 **/
@Component
@Aspect
class ParamValidation {
    @Pointcut("@annotation(cn.gjing.tools.common.annotation.NotEmpty)")
    public void cut() {
    }

    @Before("cut()")
    public void validation(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        Length length;
        Mobile mobile;
        Email email;
        NotEmpty notEmpty;
        Parameter parameter;
        for (int i = 0; i < parameters.length; i++) {
            parameter = parameters[i];
            if (parameter.isAnnotationPresent(Json.class)) {
                this.jsonCheck(args[i]);
            }
            length = parameter.getAnnotation(Length.class);
            mobile = parameter.getAnnotation(Mobile.class);
            email = parameter.getAnnotation(Email.class);
            notEmpty = parameter.getAnnotation(NotEmpty.class);
            this.valid(length, mobile, email, args[i]);
            if (parameter.isAnnotationPresent(Not.class)) {
                continue;
            }
            if (ParamUtils.isEmpty(args[i])) {
                this.validError(notEmpty == null ? parameter.getName() + " cannot be null" : notEmpty.message());
            }
        }
    }

    private void jsonCheck(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        NotEmpty notEmpty;
        Length length;
        Mobile mobile;
        Email email;
        Object value;
        for (Field field : fields) {
            notEmpty = field.getAnnotation(NotEmpty.class);
            length = field.getAnnotation(Length.class);
            mobile = field.getAnnotation(Mobile.class);
            email = field.getAnnotation(Email.class);
            value = BeanUtils.getFieldValue(o, field);
            if (notEmpty != null && ParamUtils.isEmpty(value)) {
                this.validError(notEmpty.message());
            }
            this.valid(length, mobile, email, value);
        }
    }

    private void valid(Length length, Mobile mobile, Email email, Object value) {
        if (length != null) {
            if (value == null) {
                this.validError(length.message());
            }
            if (value.toString().length() > length.value()) {
                this.validError(length.message());
            }
        }
        if (mobile != null) {
            if (value == null) {
                this.validError(mobile.message());
            }
            if (!ParamUtils.isMobileNumber(value.toString())) {
                this.validError(mobile.message());
            }
        }
        if (email != null) {
            if (value == null) {
                this.validError(email.message());
            }
            if (!ParamUtils.isEmail(value.toString())) {
                this.validError(email.message());
            }
        }
    }

    private void validError(String message) {
        throw new ParamValidException(message);
    }

}
