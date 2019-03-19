package com.gj.proxy;

import com.gj.anno.NotNull;
import com.gj.enums.HttpStatus;
import com.gj.ex.ParamException;
import com.gj.utils.GjUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Archine
 * This class is primarily used for method parsing as a notnull annotation to detect whether all parameters on a method
 * contain null values , Throws an {ParamException} if it contains null values
 **/
@Component
@Aspect
public class NotNullProxy {
    @Pointcut("@annotation(com.gj.anno.NotNull)")
    public void cut() {

    }

    @Before("cut()")
    @SuppressWarnings("unchecked")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        NotNull annotation = method.getDeclaredAnnotation(NotNull.class);
        List<String > exclude = Arrays.asList(annotation.exclude());
        List<String> needCheckParamList = new ArrayList();
        Parameter[] parameters = method.getParameters();
        if (exclude.size() < 1) {
            for (Parameter parameter : parameters) {
                needCheckParamList.add(parameter.getName());
            }
        }else {
            for (Parameter parameter : parameters) {
                if (!exclude.contains(parameter.getName())) {
                    needCheckParamList.add(parameter.getName());
                }
            }
        }
        if (GjUtil.multiParamHasEmpty(needCheckParamList.stream().map(request::getParameter).collect(Collectors.toList()))) {
            if (GjUtil.paramIsEmpty(annotation.message())) {
                throw new ParamException(HttpStatus.PARAM_EMPTY.getMsg());
            } else {
                throw new ParamException(annotation.message());
            }
        }
    }

}
