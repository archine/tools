package cn.gjing.tools.excel.write.listener;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Row lifecycle listener
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelRowWriteListener extends ExcelWriteListener {
    /**
     * All the cells for the current row are created
     *
     * @param sheet  Current sheet
     * @param row    Create the finished row
     * @param obj    Current Java object
     * @param index  Line index, index type according to isHead，Starting from 0
     * @param isHead Whether is excel head
     */
    void completeRow(Sheet sheet, Row row, Object obj, int index, boolean isHead);
}
