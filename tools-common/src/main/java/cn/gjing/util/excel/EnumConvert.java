package cn.gjing.util.excel;

/**
 * 枚举转换器
 * @author Gjing
 **/
@SuppressWarnings("unused")
public interface EnumConvert<T extends Enum> {
    /**
     * 值转枚举
     * @param e 值的数据类型
     * @return 枚举
     */
    T to(Object e);

    /**
     * 枚举转值
     * @param t 枚举
     * @return 枚举对应的值
     */
    Object from(T t);
}
