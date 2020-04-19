package cn.gjing.tools.excel.read.listener;

import cn.gjing.tools.excel.ExcelField;

import java.lang.reflect.Field;

/**
 * Reads to a nonexistent cell or the cell content is empty
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelEmptyReadListener<R> extends ExcelReadListener {

    /**
     * When a body cell is read, if the cell does not exist or the value is null,
     * and you set the header to which the cell belongs not to be null, {@link ExcelField}
     *
     * @param r          Current Java object
     * @param field      Current field
     * @param excelField ExcelField annotation on that field
     * @param rowIndex   The index of the current row
     * @param colIndex   The index of the current col
     * @return Whether to save this data
     */
    boolean readEmpty(R r, Field field, ExcelField excelField, int rowIndex, int colIndex);
}
