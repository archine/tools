package cn.gjing.tools.common.valid;

import cn.gjing.tools.common.exception.ParamValidException;
import cn.gjing.tools.common.util.ParamUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * @author Gjing
 **/
class ParamValidationHandle implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Parameter[] parameters = method.getParameters();
            NotEmpty notEmpty = method.getAnnotation(NotEmpty.class);
            NotNull notNull = method.getAnnotation(NotNull.class);
            Object value;
            boolean isFile;
            if (notEmpty != null) {
                for (Parameter parameter : parameters) {
                    isFile = parameter.getType() == MultipartFile.class;
                    value = isFile ? ((StandardMultipartHttpServletRequest) request).getMultiFileMap().get(parameter.getName()) : request.getParameter(parameter.getName());
                    if (!parameter.isAnnotationPresent(Not.class)) {
                        if (ParamUtils.isEmpty(value)) {
                            throw new ParamValidException(parameter.getName() + "不能为空");
                        }
                    }
                    if (!isFile) {
                        this.expandCheck(parameter, value.toString());
                    }
                }
                return true;
            }
            if (notNull != null) {
                for (Parameter parameter : parameters) {
                    isFile = parameter.getType() == MultipartFile.class;
                    value = isFile ? ((StandardMultipartHttpServletRequest) request).getMultiFileMap().get(parameter.getName()) : request.getParameter(parameter.getName());
                    if (!parameter.isAnnotationPresent(Not.class)) {
                        if (value == null) {
                            throw new ParamValidException(parameter.getName() + "不能为Null");
                        }
                    }
                    if (!isFile) {
                        this.expandCheck(parameter, value.toString());
                    }
                }
                return true;
            }
            for (Parameter parameter : parameters) {
                if (parameter.isAnnotationPresent(Json.class)) {
                    this.jsonValid(parameter.getType(), request, parameter.isAnnotationPresent(RequestBody.class));
                    continue;
                }
                isFile = parameter.getType() == MultipartFile.class;
                value = isFile ? ((StandardMultipartHttpServletRequest) request).getMultiFileMap().get(parameter.getName()) : request.getParameter(parameter.getName());
                notEmpty = parameter.getAnnotation(NotEmpty.class);
                if (notEmpty != null && ParamUtils.isEmpty(value)) {
                    throw new ParamValidException(notEmpty.message());
                }
                notNull = parameter.getAnnotation(NotNull.class);
                if (notNull != null && value == null) {
                    throw new ParamValidException(notNull.message());
                }
                if (!isFile) {
                    this.expandCheck(parameter, value);
                }
            }
            return true;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private void jsonValid(Class<?> c, HttpServletRequest request, boolean b) {
        Field[] fields = c.getDeclaredFields();
        if (b) {
            ParamValidationServletRequest validationRequest = (ParamValidationServletRequest) request;
            String jsonStr = validationRequest.getBody();
            Map<String, Object> valueMap;
            try {
                valueMap = new ObjectMapper().readValue(jsonStr, Map.class);
            } catch (IOException e) {
                throw new ParamValidException("无效Json对象");
            }
            for (Field field : fields) {
                this.expandCheck(field, request, valueMap, b);
            }
            return;
        }
        for (Field field : fields) {
            this.expandCheck(field, request, null, b);
        }
    }

    private void expandCheck(Field field, HttpServletRequest request, Map<String, Object> valueMap,boolean body) {
        NotNull notNull = field.getAnnotation(NotNull.class);
        NotEmpty notEmpty = field.getAnnotation(NotEmpty.class);
        Length length = field.getAnnotation(Length.class);
        Email email = field.getAnnotation(Email.class);
        Mobile mobile = field.getAnnotation(Mobile.class);
        this.expandCheck(length, email, mobile, notNull, notEmpty, body ? valueMap.get(field.getName()) : request.getParameter(field.getName()));
    }

    private void expandCheck(Parameter parameter,Object value) {
        Length length = parameter.getAnnotation(Length.class);
        Email email = parameter.getAnnotation(Email.class);
        Mobile mobile = parameter.getAnnotation(Mobile.class);
        this.expandCheck(length, email, mobile, value);
    }

    private void expandCheck(Length length, Email email, Mobile mobile,NotNull notNull,NotEmpty notEmpty,Object value) {
        if (notEmpty != null && ParamUtils.isEmpty(value)) {
            throw new ParamValidException(notEmpty.message());
        }
        if (notNull != null && value == null) {
            throw new ParamValidException(notNull.message());
        }
        this.expandCheck(length, email, mobile, value);
    }

    private void expandCheck(Length length, Email email, Mobile mobile,Object value) {
        if (length != null) {
            if (length.min() <= 0) {
                if (value != null && value.toString().length() > length.max()) {
                    throw new ParamValidException(length.message());
                }
            } else {
                if (value == null) {
                    throw new ParamValidException(length.message());
                }
                int l = value.toString().length();
                if (l > length.max() || l < length.max()) {
                    throw new ParamValidException(length.message());
                }
            }
        }
        if (email != null) {
            if (value != null && !ParamUtils.isEmail(value.toString())) {
                throw new ParamValidException(email.message());
            }
        }
        if (mobile != null) {
            if (value != null && !ParamUtils.isMobileNumber(value.toString())) {
                throw new ParamValidException(mobile.message());
            }
        }
    }
}
