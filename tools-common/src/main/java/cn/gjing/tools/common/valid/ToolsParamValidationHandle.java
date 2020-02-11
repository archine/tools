package cn.gjing.tools.common.valid;

import cn.gjing.tools.common.exception.ParamValidException;
import cn.gjing.tools.common.util.ParamUtils;
import com.fasterxml.jackson.core.type.TypeReference;
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
class ToolsParamValidationHandle implements HandlerInterceptor {
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
                    if (this.isJson(request, parameter)) {
                        continue;
                    }
                    isFile = parameter.getType() == MultipartFile.class;
                    value = isFile ? ((StandardMultipartHttpServletRequest) request).getMultiFileMap().get(parameter.getName()) : request.getParameter(parameter.getName());
                    if (!parameter.isAnnotationPresent(Not.class)) {
                        if (ParamUtils.isEmpty(value)) {
                            throw new ParamValidException(parameter.getName() + "不能为空");
                        }
                    }
                    if (isJson(request, parameter)) continue;
                    if (!isFile) {
                        this.expandCheck(parameter, value);
                    }
                }
                return true;
            }
            if (notNull != null) {
                for (Parameter parameter : parameters) {
                    if (this.isJson(request, parameter)) {
                        continue;
                    }
                    isFile = parameter.getType() == MultipartFile.class;
                    value = isFile ? ((StandardMultipartHttpServletRequest) request).getMultiFileMap().get(parameter.getName()) : request.getParameter(parameter.getName());
                    if (!parameter.isAnnotationPresent(Not.class)) {
                        if (value == null) {
                            throw new ParamValidException(parameter.getName() + "不能为Null");
                        }
                    }
                    if (!isFile) {
                        this.expandCheck(parameter, value);
                    }
                }
                return true;
            }
            for (Parameter parameter : parameters) {
                if (this.isJson(request, parameter)) {
                    continue;
                }
                isFile = parameter.getType() == MultipartFile.class;
                value = isFile ? ((StandardMultipartHttpServletRequest) request).getMultiFileMap().get(parameter.getName()) : request.getParameter(parameter.getName());
                notEmpty = parameter.getAnnotation(NotEmpty.class);
                notNull = parameter.getAnnotation(NotNull.class);
                this.emptyCheck(notNull, notEmpty, value);
                if (!isFile) {
                    this.expandCheck(parameter, value);
                }
            }
            return true;
        }
        return true;
    }

    private boolean isJson(HttpServletRequest request, Parameter parameter) {
        if (parameter.isAnnotationPresent(Json.class)) {
            Field[] fields = parameter.getType().getDeclaredFields();
            if (parameter.isAnnotationPresent(RequestBody.class)) {
                Map<String, Object> valueMap;
                try {
                    valueMap = new ObjectMapper().readValue(request.getInputStream(), new TypeReference<Map<String, Object>>() {});
                } catch (IOException e) {
                    throw new ParamValidException("无效的Json对象");
                }
                for (Field field : fields) {
                    this.expandCheck(field, request, valueMap, true);
                }
            } else {
                for (Field field : fields) {
                    this.expandCheck(field, request, null, false);
                }
            }
            return true;
        }
        return false;
    }

    private void expandCheck(Field field, HttpServletRequest request, Map<String, Object> valueMap, boolean body) {
        NotNull notNull = field.getAnnotation(NotNull.class);
        NotEmpty notEmpty = field.getAnnotation(NotEmpty.class);
        Length length = field.getAnnotation(Length.class);
        Email email = field.getAnnotation(Email.class);
        Mobile mobile = field.getAnnotation(Mobile.class);
        Object v = body ? valueMap.get(field.getName()) : request.getParameter(field.getName());
        this.emptyCheck(notNull, notEmpty, v);
        this.expandCheck(length, email, mobile, v);
    }

    private void expandCheck(Parameter parameter, Object v) {
        Length length = parameter.getAnnotation(Length.class);
        Email email = parameter.getAnnotation(Email.class);
        Mobile mobile = parameter.getAnnotation(Mobile.class);
        this.expandCheck(length, email, mobile, v);
    }

    private void emptyCheck(NotNull notNull, NotEmpty notEmpty, Object value) {
        if (notEmpty != null && ParamUtils.isEmpty(value)) {
            throw new ParamValidException(notEmpty.message());
        }
        if (notNull != null && value == null) {
            throw new ParamValidException(notNull.message());
        }
    }

    private void expandCheck(Length length, Email email, Mobile mobile, Object value) {
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
