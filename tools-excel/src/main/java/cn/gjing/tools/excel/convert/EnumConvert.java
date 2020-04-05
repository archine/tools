package cn.gjing.tools.excel.convert;

/**
 * Enumeration converter
 *
 * @author Gjing
 **/
public interface EnumConvert<T extends Enum<?>, E> {
    /**
     * Converted to entity enum field
     *
     * @param e Excel cell value
     * @return enum
     */
    T toEntityAttribute(E e);

    /**
     * Converted to excel value
     *
     * @param t t Current field value
     * @return E
     */
    E toExcelAttribute(T t);

}
