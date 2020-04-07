package cn.gjing.tools.excel.write.callback;

import java.lang.reflect.Field;

/**
 * @author Gjing
 **/
public interface AutoColumnMergeCallback<T> {
    /**
     * This callback occurs from the second Excel mapping entity
     *
     * @param t        Current excel mapping entity
     * @param field    Current field
     * @param colIndex colIndex
     * @param rowIndex rowIndex
     * @return boolean
     */
    boolean toMerge(T t, Field field, int colIndex, int rowIndex);

    /**
     * This callback occurs when the Excel mapping entity is the first
     *
     * @param t Current excel mapping entity
     */
    default void init(T t) {}
}
