package cn.gjing.tools.excel.convert;

import java.lang.reflect.Field;

/**
 * @author Gjing
 **/
public interface DataConvert<T> {
    /**
     * Convert to an entity field
     *
     * @param value      Excel cell value
     * @param field      Current field
     * @return new value
     */
    Object toEntityAttribute(Object value, Field field);

    /**
     * Convert to excel cell value
     *
     * @param obj        Current excel entity
     * @param value      The value of the current field
     * @param field      Current field
     * @return new value
     */
    Object toExcelAttribute(T obj, Object value, Field field);
}
