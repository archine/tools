package cn.gjing.handle;

import cn.gjing.ParamUtil;
import cn.gjing.annotation.NotNull;
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
        List<String > exclude = Arrays.asList(annotation.exclude());
        //定义一个需要检查的参数列表
        List<String> needCheckParamList = new ArrayList();
        //方法的参数
        Parameter[] parameters = method.getParameters();
        //如果使用占位符模式,则跳过
        if (ParamUtil.isEmpty(request.getQueryString())) {
            System.out.println("-------------------1");
            if (exclude.isEmpty()) {
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
            if (ParamUtil.listIncludeEmpty(needCheckParamList.stream().map(request::getParameter).collect(Collectors.toList()))) {
                throw new NullPointerException("The parameter @NotNull is used, so null is not allowed");
            }
        }

    }

}
