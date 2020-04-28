package cn.gjing.tools.excel.convert;

import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * BigDecimal and string conversion, for Excel as a string or number ..., Java as a BigDecimal
 * @author Gjing
 **/
public class DecimalConvert implements DataConvert<Object> {
    @Override
    public Object toEntityAttribute(Object value, Field field) {
        return StringUtils.isEmpty(value) ? BigDecimal.ZERO : new BigDecimal(value.toString());
    }
    @Override
    public Object toExcelAttribute(Object obj, Object value, Field field) {
        return value;
    }
}
