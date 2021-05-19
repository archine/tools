package cn.gjing.tools.excel.convert;

import org.springframework.util.StringUtils;

/**
 * Converts the string to a Java Long
 * @author Gjing
 **/
public class LongConvert implements DataConvert<Object> {
    @Override
    public Object toEntityAttribute(Object entity, Object value) {
        return StringUtils.isEmpty(value) ? 0L : Long.parseLong(value.toString());
    }

    @Override
    public Object toExcelAttribute(Object entity, Object value) {
        return value;
    }
}
