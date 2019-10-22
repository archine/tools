package cn.gjing.tools.excel;

/**
 * 枚举转换器
 *
 * @author Gjing
 **/
public interface EnumConvert<T extends Enum, U> {
    /**
     * 转为实体类的枚举字段
     *
     * @param u 读到的Excel的值
     * @return 枚举
     */
    T toEntityAttribute(U u);

    /**
     * 转成Excel的值
     *
     * @param t 枚举
     * @return 枚举对应的值
     */
    U toExcelAttribute(T t);

}
