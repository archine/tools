package cn.gjing.tools.excel.write.callback;

import cn.gjing.tools.excel.write.resolver.ExcelBindWriter;
import cn.gjing.tools.excel.write.resolver.ExcelSimpleWriter;

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
     * @param field      The corresponding entity field, Exists only if the export type is bind {@link ExcelBindWriter}，otherwise is null
     * @param headerName The corresponding header name, Exists only if the export type is simple {@link ExcelSimpleWriter}，otherwise is null
     * @param colIndex   Current column Index
     * @param index      The data index, start at 0
     * @return true is need merge
     */
    boolean mergeY(T entity, Field field, String headerName, int colIndex, int index);
}
