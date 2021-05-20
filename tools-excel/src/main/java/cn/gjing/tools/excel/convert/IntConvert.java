package cn.gjing.tools.excel.convert;

import org.springframework.util.StringUtils;

/**
 * Convert the string to a Java integer
 *
 * @author Gjing
 **/
public class IntConvert implements DataConvert<Object> {
    @Override
    public Object toEntityAttribute(Object entity, Object value) {
        return StringUtils.isEmpty(value) ? 0 : Integer.parseInt(value.toString());
    }

    @Override
    public Object toExcelAttribute(Object entity, Object value) {
        return value;
    }
}
