package com.gj.proxy;

import com.gj.anno.NotNull;
import com.gj.enums.HttpStatus;
import com.gj.ex.ParamException;
import com.gj.utils.GjUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author Archine
 * This class is primarily used to detect whether a parameter marked NotNull has a null value
 * Throws an {ParamException} if it contains null values
 **/
@Slf4j
public class NotNullMethodParamHandler implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(NotNull.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String param = nativeWebRequest.getParameter(methodParameter.getParameterName());
        if (GjUtil.paramIsEmpty(param)) {
            throw new ParamException(HttpStatus.PARAM_EMPTY.getMsg());
        }
        return param;
    }
}
