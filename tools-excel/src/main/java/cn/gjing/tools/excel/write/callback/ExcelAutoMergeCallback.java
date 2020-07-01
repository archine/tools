package cn.gjing.tools.excel.write.callback;

import java.lang.reflect.Field;

/**
 * Auto merge callback
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelAutoMergeCallback<T> {
    /**
     * Vertical merge
     *
     * @param t        The excel mapping entity for the current row, Null in the header case
     * @param field    The field corresponding to the current callback has a value only when the simple type is exported
     * @param key      The key corresponding to the current callback has a value only when the simple type is exported
     * @param colIndex colIndex
     * @param index    Line index, index type according to isHead
     * @return true is need merge
     */
    boolean mergeY(T t, Field field, String key, int colIndex, int index);
}
