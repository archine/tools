package cn.gjing.tools.excel.write.listener;

import cn.gjing.tools.excel.ExcelField;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;

/**
 * Cell lifestyle listener
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelCellWriteListener extends ExcelWriteListener {
    /**
     * Cell assignment complete,the current cell end
     *
     * @param sheet      Current sheet
     * @param row        Current row
     * @param cell       Current cell
     * @param index      Line index, index type according to isHead, Starting from 0
     * @param colIndex   Current cell index
     * @param isHead     Whether is excel head
     * @param excelField ExcelField annotation of current field
     * @param field      Current field
     */
    void completeCell(Sheet sheet, Row row, Cell cell, ExcelField excelField, Field field, int index, int colIndex, boolean isHead);

    /**
     * Cell are assigned before, then after the data converter and before the auto merge
     *
     * @param sheet      Current sheet
     * @param row        Current row
     * @param cell       Current cell
     * @param index      Line index, index type according to isHead, Starting from 0
     * @param colIndex   Current cell index
     * @param isHead     Whether is excel head
     * @param excelField ExcelField annotation of current field
     * @param field      Current field
     * @param value      Cell value
     * @return Cell value
     */
    default Object assignmentBefore(Sheet sheet, Row row, Cell cell, ExcelField excelField, Field field, int index, int colIndex, boolean isHead, Object value) {
        return value;
    }
}
