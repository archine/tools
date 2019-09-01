package cn.gjing.util.excel;

/**
 * 枚举转换器
 * @author Gjing
 **/
public interface EnumConvert<T extends Enum,E extends Object> {
    /**
     * 值转枚举
     * @param e 值的数据类型
     * @return 枚举
     */
    T to(E e);

    /**
     * 枚举转值
     * @param t 枚举
     * @return 枚举对应的值
     */
    E from(T t);
}
