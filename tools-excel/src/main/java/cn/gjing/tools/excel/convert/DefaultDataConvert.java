package cn.gjing.tools.excel.convert;

import java.lang.reflect.Field;

/**
 * @author Gjing
 **/
public class DefaultDataConvert implements DataConvert<Object> {

    @Override
    public Object toEntityAttribute(Object value, Field field) {
        return value;
    }

    @Override
    public Object toExcelAttribute(Object obj, Object value, Field field) {
        return value;
    }
}
