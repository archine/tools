package cn.gjing.tools.excel.convert;

/**
 * @author Gjing
 **/
public class DefaultDataConvert implements DataConvert<Object> {

    @Override
    public Object toEntityAttribute(Object entity, Object value) {
        return value;
    }

    @Override
    public Object toExcelAttribute(Object entity, Object value) {
        return value;
    }
}
