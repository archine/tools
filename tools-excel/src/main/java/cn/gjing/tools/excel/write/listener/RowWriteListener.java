package cn.gjing.tools.excel.write.listener;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Row lifecycle listener
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface RowWriteListener extends WriteListener {
    /**
     * All the cells for the current row are created
     *
     * @param sheet  Current sheet
     * @param row    Create the finished row
     * @param index  Line index, index type according to isHead
     * @param isHead Whether is excel head
     */
    void completeRow(Sheet sheet, Row row, int index, boolean isHead);
}
