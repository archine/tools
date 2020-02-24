package cn.gjing.tools.excel;

import java.lang.reflect.Field;

/**
 * @author Gjing
 **/
public interface ReadCallback<T> {
    /**
     * Read each line for a callback
     *
     * @param value    Get the object
     * @param rowIndex The index of the current row
     * @return value
     */
    default T readLine(T value, int rowIndex) {
        return value;
    }

    /**
     * The method callback occurs when the cell data is empty and the policy is set to jump
     *
     * @param field Empty field
     * @param excelField ExcelField annotation on that field
     * @param rowIndex The index of the current row
     * @param colIndex The index of the current col
     */
    default void readJump(Field field, ExcelField excelField, int rowIndex, int colIndex) {
    }
}
