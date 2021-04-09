package cn.gjing.tools.excel.write.listener;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.metadata.RowType;
import cn.gjing.tools.excel.metadata.listener.ExcelWriteListener;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;

/**
 * The Excel cell writes out the listener,
 * Can be used to change the value of a cell or to make additional extensions to the cell when exporting
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelCellWriteListener extends ExcelWriteListener {
    /**
     * Cell assignment complete,the current cell end, About to start reading the next cell
     *
     * @param sheet      Current sheet
     * @param row        Current row
     * @param cell       Current cell
     * @param index      Data indexing, depending on the row type, starts at 0
     * @param colIndex   Current cell index
     * @param rowType    Current row type
     * @param excelField ExcelField annotation of current field
     * @param field      Current field
     */
    void completeCell(Sheet sheet, Row row, Cell cell, ExcelField excelField, Field field, int index, int colIndex, RowType rowType);

    /**
     * Cell are assigned before, then after the data converter and before the auto merge
     *
     * @param sheet      Current sheet
     * @param row        Current row
     * @param cell       Current cell
     * @param index      Data indexing, depending on the row type, starts at 0
     * @param colIndex   Current cell index
     * @param rowType    Current row type
     * @param excelField ExcelField annotation of current field
     * @param field      Current field
     * @param value      Cell value
     * @return Cell value, If null, no assignment will take place
     */
    default Object assignmentBefore(Sheet sheet, Row row, Cell cell, ExcelField excelField, Field field, int index, int colIndex, RowType rowType, Object value) {
        return value;
    }
}
