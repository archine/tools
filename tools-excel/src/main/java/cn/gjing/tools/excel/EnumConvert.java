package cn.gjing.tools.excel;

/**
 * 枚举转换器
 *
 * @author Gjing
 **/
public interface EnumConvert<T extends Enum, U> {
    /**
     * 转为实体类字段
     *
     * @param t 值的数据类型
     * @return 枚举
     */
    T toEntityAttribute(String t);

    /**
     * 转成数据库字段
     *
     * @param t 枚举
     * @return 枚举对应的值
     */
    U toExcelAttribute(T t);

}
