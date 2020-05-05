package cn.gjing.tools.excel.write.style;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.listener.ExcelCellWriteListener;
import cn.gjing.tools.excel.write.listener.ExcelRowWriteListener;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;

/**
 * Excel style listener
 *
 * @author Gjing
 **/
public interface ExcelStyleWriteListener extends ExcelCellWriteListener, ExcelRowWriteListener {

    /**
     * Init Style write listener
     *
     * @param workbook workbook
     */
    void init(Workbook workbook);


    /**
     * Set excel big title style
     *
     * @param cell Current cell
     * @param bigTitle Bit title
     */
    void setTitleStyle(BigTitle bigTitle, Cell cell);

    /**
     * Set excel head style
     *
     * @param row        Current row
     * @param cell       Current cell
     * @param index      Line index, index type according to isHead
     * @param colIndex   cell index
     * @param excelField ExcelField annotation of current field
     * @param field      Current field
     */
    void setHeadStyle(Row row, Cell cell, ExcelField excelField, Field field, int index, int colIndex);

    /**
     * Set excel body style
     *
     * @param row        Current row
     * @param cell       Current cell
     * @param index      Line index, index type according to isHead
     * @param colIndex   cell index
     * @param excelField ExcelField annotation of current field
     * @param field      Current field
     */
    void setBodyStyle(Row row, Cell cell, ExcelField excelField, Field field, int index, int colIndex);
}
