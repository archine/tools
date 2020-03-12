package cn.gjing.tools.excel.convert;

/**
 * Enumeration converter
 *
 * @author Gjing
 **/
public interface EnumConvert<T extends Enum<?>, U> {
    /**
     * Converted to entity enum field
     *
     * @param u u
     * @return enum
     */
    T toEntityAttribute(U u);

    /**
     * Converted to excel value
     *
     * @param t t
     * @return u
     */
    U toExcelAttribute(T t);

}
