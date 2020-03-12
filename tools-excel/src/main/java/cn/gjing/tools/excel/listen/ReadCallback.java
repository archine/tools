package cn.gjing.tools.excel.listen;

import cn.gjing.tools.excel.ExcelField;
import java.lang.reflect.Field;

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
     * The method callback occurs when the cell data is empty and the policy is set to jump
     *
     * @param field      Empty field
     * @param excelField ExcelField annotation on that field
     * @param rowIndex   The index of the current row
     * @param colIndex   The index of the current col
     */
    default void readJump(Field field, ExcelField excelField, int rowIndex, int colIndex) {
    }

    /**
     * Triggered when a reading is not empty cell
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
