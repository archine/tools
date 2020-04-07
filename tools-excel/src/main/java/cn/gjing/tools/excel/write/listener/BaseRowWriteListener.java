package cn.gjing.tools.excel.write.listener;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Row lifecycle listener
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface BaseRowWriteListener extends WriteListener {
    /**
     * All the cells for the current row are created
     *
     * @param sheet    Current sheet
     * @param row      Create the finished row
     * @param rowIndex Current row index
     * @param isHead   Whether is excel head
     */
    void createdRow(Sheet sheet, Row row, int rowIndex, boolean isHead);
}
