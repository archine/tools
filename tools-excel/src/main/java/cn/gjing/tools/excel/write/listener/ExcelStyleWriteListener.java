package cn.gjing.tools.excel.write.listener;

import cn.gjing.tools.excel.metadata.ExcelFieldProperty;
import cn.gjing.tools.excel.metadata.listener.ExcelWriteListener;
import cn.gjing.tools.excel.write.BigTitle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Excel style listener
 *
 * @author Gjing
 **/
public interface ExcelStyleWriteListener extends ExcelWriteListener {
    /**
     * Set excel big title style
     *
     * @param cell     Current cell
     * @param bigTitle Bit title
     */
    void setTitleStyle(BigTitle bigTitle, Cell cell);

    /**
     * Set excel head style
     *
     * @param row      Current row
     * @param cell     Current cell
     * @param index    Line index, index type according to isHead
     * @param colIndex cell index
     * @param property ExcelField property of current field
     */
    void setHeadStyle(Row row, Cell cell, ExcelFieldProperty property, int index, int colIndex);

    /**
     * Set excel body style
     *
     * @param row      Current row
     * @param cell     Current cell
     * @param index    Line index, index type according to isHead
     * @param colIndex cell index
     * @param property ExcelField property of current field
     */
    void setBodyStyle(Row row, Cell cell, ExcelFieldProperty property, int index, int colIndex);
}
