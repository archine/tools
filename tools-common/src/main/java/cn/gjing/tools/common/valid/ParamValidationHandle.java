package cn.gjing.tools.common.valid;

import cn.gjing.tools.common.exception.ParamValidException;
import cn.gjing.tools.common.util.ParamUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Gjing
 **/
class ParamValidationHandle implements HandlerInterceptor {
    private NotEmpty notEmpty;
    private NotNull notNull;
    private Email email;
    private Length length;
    private Mobile mobile;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Parameter[] parameters = method.getParameters();
            this.notEmpty = method.getAnnotation(NotEmpty.class);
            String value;
            if (this.notEmpty != null) {
                for (Parameter parameter : parameters) {
                    value = request.getParameter(parameter.getName());
                    if (parameter.isAnnotationPresent(Not.class)) {
                        this.expandCheck(parameter, value);
                    } else {
                        if (ParamUtils.isEmpty(value)) {
                            throw new ParamValidException(parameter.getName() + "不能为空");
                        }
                        this.expandCheck(parameter, value);
                    }
                }
                return true;
            }
            this.notNull = method.getAnnotation(NotNull.class);
            if (this.notNull != null) {
                for (Parameter parameter : parameters) {
                    value = request.getParameter(parameter.getName());
                    if (parameter.isAnnotationPresent(Not.class)) {
                        this.expandCheck(parameter, value);
                    } else {
                        if (value == null) {
                            throw new ParamValidException(parameter.getName() + "不能为Null");
                        }
                        this.expandCheck(parameter, value);
                    }
                }
                return true;
            }
            for (Parameter parameter : parameters) {
                if (parameter.isAnnotationPresent(Json.class)) {
                    this.jsonValid(parameter.getType(), request, parameter.isAnnotationPresent(RequestBody.class));
                    continue;
                }
                value = request.getParameter(parameter.getName());
                this.notEmpty = parameter.getAnnotation(NotEmpty.class);
                if (this.notEmpty != null && ParamUtils.isEmpty(value)) {
                    throw new ParamValidException(notEmpty.message());
                }
                notNull = parameter.getAnnotation(NotNull.class);
                if (notNull != null && value == null) {
                    throw new ParamValidException(notNull.message());
                }
                this.expandCheck(parameter, value);
            }
            return true;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private void jsonValid(Class<?> c, HttpServletRequest request, boolean b) {
        Field[] fields = c.getDeclaredFields();
        if (b) {
            String jsonStr = "";
            try {
                jsonStr = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Map<String,Object> valueMap;
            try {
                valueMap = new ObjectMapper().readValue(jsonStr, Map.class);
            } catch (IOException e) {
                throw new ParamValidException("无效Json对象");
            }
            for (Field field : fields) {
                this.expandCheck(field, valueMap.get(field.getName()));
            }
            return;
        }
        for (Field field : fields) {
            this.expandCheck(field, request.getParameter(field.getName()));
        }
    }

    private void expandCheck(Parameter parameter, String value) {
        this.length = parameter.getAnnotation(Length.class);
        if (this.length != null) {
            if (this.length.min() <= 0) {
                if (value != null && value.length() > this.length.max()) {
                    throw new ParamValidException(this.length.message());
                }
            } else {
                if (value == null) {
                    throw new ParamValidException(this.length.message());
                }
                int l = value.length();
                if (l > this.length.max() || l < this.length.max()) {
                    throw new ParamValidException(this.length.message());
                }
            }
        }
        this.email = parameter.getAnnotation(Email.class);
        if (this.email != null) {
            if (value != null && !ParamUtils.isEmail(value)) {
                throw new ParamValidException(this.email.message());
            }
        }
        this.mobile = parameter.getAnnotation(Mobile.class);
        if (this.mobile != null) {
            if (value != null && ParamUtils.isMobileNumber(value)) {
                throw new ParamValidException(this.mobile.message());
            }
        }
    }

    private void expandCheck(Field field, Object value) {
        this.notEmpty = field.getAnnotation(NotEmpty.class);
        if (this.notEmpty != null && ParamUtils.isEmpty(value)) {
            throw new ParamValidException(notEmpty.message());
        }
        this.notNull = field.getAnnotation(NotNull.class);
        if (this.notNull != null && value == null) {
            throw new ParamValidException(notNull.message());
        }
        this.length = field.getAnnotation(Length.class);
        if (this.length != null) {
            if (this.length.min() <= 0) {
                if (value != null && value.toString().length() > this.length.max()) {
                    throw new ParamValidException(this.length.message());
                }
            } else {
                if (value == null) {
                    throw new ParamValidException(this.length.message());
                }
                int l = value.toString().length();
                if (l > this.length.max() || l < this.length.max()) {
                    throw new ParamValidException(this.length.message());
                }
            }
        }
        this.email = field.getAnnotation(Email.class);
        if (this.email != null) {
            if (value != null && !ParamUtils.isEmail(value.toString())) {
                throw new ParamValidException(this.email.message());
            }
        }
        this.mobile = field.getAnnotation(Mobile.class);
        if (this.mobile != null) {
            if (value != null && ParamUtils.isMobileNumber(value.toString())) {
                throw new ParamValidException(this.mobile.message());
            }
        }
    }
}
