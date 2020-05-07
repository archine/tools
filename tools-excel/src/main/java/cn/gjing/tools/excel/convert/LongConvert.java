package cn.gjing.tools.excel.convert;

import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * Converts the string to a Java Long
 * @author Gjing
 **/
public class LongConvert implements DataConvert<Object> {
    @Override
    public Object toEntityAttribute(Object value, Field field) {
        return StringUtils.isEmpty(value) ? 0L : Long.parseLong(value.toString());
    }

    @Override
    public Object toExcelAttribute(Object obj, Object value, Field field) {
        return value;
    }
}
