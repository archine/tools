package cn.gjing.tools.excel.write.callback;

import java.lang.reflect.Field;

/**
 * Auto merge callback
 *
 * @author Gjing
 **/
public interface AutoMergeCallback<T> {
    /**
     * Vertical merger
     * This callback occurs from the second Excel mapping entity
     *
     * @param t        The excel mapping entity for the current row, Null in the header case
     * @param field    Current field
     * @param colIndex colIndex
     * @param index    Line index, index type according to isHead
     * @param isHead   Whether is head
     * @return true is need merge
     */
    boolean mergeY(T t, Field field, int colIndex, int index, boolean isHead);

    /**
     * Horizontal merger
     *
     * @param t        The excel mapping entity for the current row, Null in the header case
     * @param field    Current field
     * @param colIndex colIndex
     * @param index    Line index, index type according to isHead
     * @param isHead   Whether is head
     * @return true is need merge
     */
    default boolean mergeX(T t, Field field, int colIndex, int index, boolean isHead) {
        return isHead;
    }
}
