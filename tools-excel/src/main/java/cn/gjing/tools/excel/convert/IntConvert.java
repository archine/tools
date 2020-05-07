package cn.gjing.tools.excel.convert;

import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * Convert the string to a Java integer
 *
 * @author Gjing
 **/
public class IntConvert implements DataConvert<Object> {
    @Override
    public Object toEntityAttribute(Object value, Field field) {
        return StringUtils.isEmpty(value) ? 0 : Integer.parseInt(value.toString());
    }

    @Override
    public Object toExcelAttribute(Object obj, Object value, Field field) {
        return value;
    }
}
