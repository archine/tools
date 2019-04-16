package cn.gjing.handle;

import cn.gjing.ParamUtil;
import cn.gjing.annotation.NotNull;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Gjing
 * This class is primarily used for method parsing as a notnull annotation to detect whether all parameters on a method
 * contain null values , Throws an {ParamException} if it contains null values
 **/
@Component
@Aspect
class NotNullProcessor {
    @Pointcut("@annotation(cn.gjing.annotation.NotNull)")
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
        //排除的参数
        List<String> exclude = Arrays.asList(annotation.exclude());
        //定义一个需要检查的参数列表
        List<String> needCheckParamList = new ArrayList();
        //方法的参数
        Parameter[] parameters = method.getParameters();
        if (exclude.isEmpty()) {
            for (Parameter parameter : parameters) {
                if (!parameter.getType().isPrimitive()&&parameter.getAnnotation(PathVariable.class)==null) {
                    needCheckParamList.add(parameter.getName());
                }
            }
        } else {
            for (Parameter parameter : parameters) {
                if (!exclude.contains(parameter.getName()) && !parameter.getType().isPrimitive()&&parameter.getAnnotation(PathVariable.class)==null) {
                    needCheckParamList.add(parameter.getName());
                }
            }
        }
        for (String paramName : needCheckParamList) {
            if (ParamUtil.isEmpty(request.getParameter(paramName))) {
                throw new NullPointerException("The parameter '"+paramName+"' has been used @NotNull, so it cannot be null.");
            }
        }
    }

}
