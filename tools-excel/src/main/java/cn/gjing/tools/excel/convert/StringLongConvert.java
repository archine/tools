package cn.gjing.tools.excel.convert;

import java.lang.reflect.Field;

/**
 * Long and string conversion, for Excel as a string, Java as a Long
 *
 * @author Gjing
 **/
public class StringLongConvert implements DataConvert<Object> {
    @Override
    public Object toEntityAttribute(Object value, Field field) {
        if (value == null) {
            return field.getClass().isPrimitive() ? 0L : null;
        }
        return Long.parseLong(value.toString());
    }

    @Override
    public Object toExcelAttribute(Object obj, Object value, Field field) {
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }
}
