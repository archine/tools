package cn.gjing.tools.common.valid;

import cn.gjing.tools.common.exception.ParamValidException;
import cn.gjing.tools.common.util.BeanUtils;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
                    value = isFile ? ((StandardMultipartHttpServletRequest) request).getMultiFileMap().get(parameter.getName()) : request.getParameterValues(parameter.getName());
                    if (!parameter.isAnnotationPresent(Not.class)) {
                        if (ParamUtils.isEmpty(value)) {
                            throw new ParamValidException(parameter.getName() + "不能为空");
                        }
                    }
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
                    value = isFile ? ((StandardMultipartHttpServletRequest) request).getMultiFileMap().get(parameter.getName()) : request.getParameterValues(parameter.getName());
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
                value = isFile ? ((StandardMultipartHttpServletRequest) request).getMultiFileMap().get(parameter.getName()) : request.getParameterValues(parameter.getName());
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean isJson(HttpServletRequest request, Parameter parameter) {
        if (parameter.isAnnotationPresent(Json.class)) {
            if (parameter.isAnnotationPresent(RequestBody.class)) {
                Field[] fields = parameter.getType().getDeclaredFields();
                Map<String, Object> valueMap;
                try {
                    valueMap = new ObjectMapper().readValue(request.getInputStream(), new TypeReference<Map<String, Object>>() {
                    });
                } catch (IOException e) {
                    throw new ParamValidException("无效的Json对象");
                }
                for (Field field : fields) {
                    Object value = valueMap.get(field.getName());
                    if (field.isAnnotationPresent(Json.class)) {
                        this.emptyCheck(field.getAnnotation(NotNull.class), field.getAnnotation(NotEmpty.class), value);
                        if (value instanceof Collection) {
                            List<Map<String, Object>> nestValueList = (List) value;
                            Field[] nestObjFields = BeanUtils.getGenericType(field.getGenericType(), 0).getDeclaredFields();
                            for (Map<String, Object> nestValueMap : nestValueList) {
                                for (Field objField : nestObjFields) {
                                    this.jsonCheck(objField, nestValueMap.get(objField.getName()));
                                }
                            }
                        } else {
                            if (value != null) {
                                Field[] nestObjFields = field.getType().getDeclaredFields();
                                Map<String, ?> nestObjMap = (Map<String, ?>) value;
                                for (Field objField : nestObjFields) {
                                    this.jsonCheck(objField, nestObjMap.get(objField.getName()));
                                }
                            }
                        }
                        continue;
                    }
                    this.jsonCheck(field, value);
                }
            }
            return true;
        }
        return false;
    }

    private void jsonCheck(Field field, Object value) {
        this.emptyCheck(field.getAnnotation(NotNull.class), field.getAnnotation(NotEmpty.class), value);
        this.expandCheck(field.getAnnotation(Length.class), field.getAnnotation(Email.class), field.getAnnotation(Mobile.class),
                field.getAnnotation(Decimal.class), field.getAnnotation(Range.class), field.getAnnotation(Regex.class), value);
    }

    private void emptyCheck(NotNull notNull, NotEmpty notEmpty, Object value) {
        if (notEmpty != null && ParamUtils.isEmpty(value)) {
            throw new ParamValidException(notEmpty.message());
        }
        if (notNull != null && value == null) {
            throw new ParamValidException(notNull.message());
        }
    }

    private void expandCheck(Parameter parameter, Object value) {
        this.expandCheck(parameter.getAnnotation(Length.class), parameter.getAnnotation(Email.class), parameter.getAnnotation(Mobile.class), parameter.getAnnotation(Decimal.class),
                parameter.getAnnotation(Range.class), parameter.getAnnotation(Regex.class), value);
    }

    private void expandCheck(Length length, Email email, Mobile mobile, Decimal decimal, Range range, Regex regex, Object value) {
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
                if (l > length.max() || l < length.min()) {
                    throw new ParamValidException(length.message());
                }
            }
        }
        if (value != null) {
            String valueStr = value.toString();
            if (email != null && !ParamUtils.isEmail(valueStr)) {
                throw new ParamValidException(email.message());
            }
            if (mobile != null && !ParamUtils.isMobileNumber(valueStr)) {
                throw new ParamValidException(mobile.message());
            }
            if (range != null) {
                if (valueStr.contains(".") || !ParamUtils.isNumber(valueStr)) {
                    throw new ParamValidException(range.message());
                }
                int valueInt = Integer.parseInt(valueStr);
                if (valueInt < range.min() || valueInt > range.max()) {
                    throw new ParamValidException(range.message());
                }
            }
            if (decimal != null) {
                if (!ParamUtils.isNumber(valueStr)) {
                    throw new ParamValidException(decimal.message());
                }
                String[] valueArr = valueStr.split("\\.");
                if (valueArr[0].length() > decimal.scale() || (valueArr.length == 2 && valueArr[1].length() > decimal.prec())) {
                    throw new ParamValidException(decimal.message());
                }
            }
            if (regex != null) {
                if (!Pattern.compile(regex.expr()).matcher(value.toString()).matches()) {
                    throw new ParamValidException(regex.message());
                }
            }
        }
    }
}
