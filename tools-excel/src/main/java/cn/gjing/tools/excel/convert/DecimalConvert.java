package cn.gjing.tools.excel.convert;

import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * Converts the string to a Java BigDecimal
 *
 * @author Gjing
 **/
public class DecimalConvert implements DataConvert<Object> {
    @Override
    public Object toEntityAttribute(Object entity, Object value) {
        return StringUtils.isEmpty(value) ? BigDecimal.ZERO : new BigDecimal(value.toString());
    }

    @Override
    public Object toExcelAttribute(Object entity, Object value) {
        return value;
    }
}
