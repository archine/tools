package cn.gjing.tools.excel;

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
}
