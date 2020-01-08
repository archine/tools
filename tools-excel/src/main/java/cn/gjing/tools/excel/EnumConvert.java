package cn.gjing.tools.excel;

/**
 * Enumeration converter
 *
 * @author Gjing
 **/
public interface EnumConvert<T extends Enum<?>, E> {
    /**
     * Converted to entity enum field
     *
     * @param e Excel value
     * @return Entity value
     */
    T toEntityAttribute(E e);

    /**
     * Converted to excel value
     *
     * @param t Entity value
     * @return E Excel value
     */
    E toExcelAttribute(T t);
}
