package cn.gjing.tools.excel.write.style;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.write.listener.BaseCellWriteListener;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;

/**
 * Excel style listener
 *
 * @author Gjing
 **/
public interface BaseExcelStyleListener extends BaseCellWriteListener {
    /**
     * Init
     *
     * @param workbook workbook
     */
    void init(Workbook workbook);

    /**
     * Set excel big title style
     *
     * @param cell cell
     */
    void setTitleStyle(Cell cell);

    /**
     * Set excel head style
     *
     * @param row      Current row
     * @param cell     Current cell
     * @param rowIndex Row index
     * @param colIndex cell index
     * @param excelField ExcelField annotation of current field
     * @param field Current field
     * @param headName The header name of the list where the cell resides
     */
    void setHeadStyle(Row row, Cell cell, ExcelField excelField, Field field, String headName, int rowIndex, int colIndex);

    /**
     * Set excel body style
     *
     * @param row      Current row
     * @param cell     Current cell
     * @param rowIndex Row index
     * @param colIndex cell index
     * @param excelField ExcelField annotation of current field
     * @param field Current field
     * @param headName The header name of the list where the cell resides
     */
    void setBodyStyle(Row row, Cell cell, ExcelField excelField, Field field, String headName, int rowIndex, int colIndex);
}
