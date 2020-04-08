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
public interface CellWriteListener extends WriteListener {
    /**
     * Cell assignment complete (include data convert complete)
     *
     * @param sheet      Current sheet
     * @param row        Current row
     * @param cell       Current cell
     * @param index      Line index, index type according to isHead
     * @param colIndex   Current cell index
     * @param isHead     Whether is excel head
     * @param excelField ExcelField annotation of current field
     * @param field      Current field
     * @param value      Cell value
     * @param headName   The header name of the list where the cell resides
     */
    void completeCell(Sheet sheet, Row row, Cell cell, ExcelField excelField, Field field, String headName, int index, int colIndex, boolean isHead, Object value);
}
