package cn.gjing.tools.excel.convert;

/**
 * A data converter that converts the value of a specified field to the content of a cell read
 *
 * @author Gjing
 **/
public interface DataConvert<T> {
    /**
     * Convert to an entity field,Executes before the listener
     *
     * @param value  Excel cell value
     * @param entity Current generated excel entity
     * @return new value
     */
    Object toEntityAttribute(T entity, Object value);

    /**
     * Convert to excel cell value,Executes before the listener
     *
     * @param entity Current excel entity
     * @param value  The value of the current field
     * @return new value
     */
    Object toExcelAttribute(T entity, Object value);
}
