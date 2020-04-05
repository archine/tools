package cn.gjing.tools.excel.listen;

import cn.gjing.tools.excel.ExcelField;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Gjing
 **/
@FunctionalInterface
public interface ReadCallback<R> {
    /**
     * Read each line for a callback
     *
     * @param val      Get the object
     * @param rowIndex The index of the current row
     * @return R
     */
    R readLine(R val, int rowIndex);

    /**
     * All data current read, including the current
     *
     * @param dataList Current all data
     * @param rowIndex The index of the current row
     * @param hasNext Have next one
     */
    default void currentData(List<R> dataList, int rowIndex, boolean hasNext) {

    }

    /**
     * This callback occurs when the currently read cell does not exist or the value is null and the current header is set to not allow null
     *
     * @param field      Current field
     * @param excelField ExcelField annotation on that field
     * @param rowIndex   The index of the current row
     * @param colIndex   The index of the current col
     * @return Whether to save this data
     */
    default boolean readEmpty(Field field, ExcelField excelField, int rowIndex, int colIndex) {
        return false;
    }

    /**
     * Occurs when the currently read cell exists
     * @param val Current cell value
     * @param field Current field
     * @param rowIndex Current row index
     * @param colIndex Current col index
     * @return Object
     */
    default Object readCol(Object val, Field field, int rowIndex, int colIndex) {
        return val;
    }
}
