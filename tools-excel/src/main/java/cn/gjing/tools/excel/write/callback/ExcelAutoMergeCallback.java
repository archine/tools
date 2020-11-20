package cn.gjing.tools.excel.write.callback;

import java.lang.reflect.Field;

/**
 * Body automatic merge callback
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelAutoMergeCallback<T> {
    /**
     * Vertical merge
     *
     * @param entity     The excel mapping entity for the current row
     * @param field      The corresponding entity field, only when the bind type is exported exist，otherwise is null
     * @param headerName The corresponding header name, only when the simple type is exported exist，otherwise is null
     * @param colIndex   Current column Index
     * @param index      The data index, start at 0
     * @return true is need merge
     */
    boolean mergeY(T entity, Field field, String headerName, int colIndex, int index);
}
