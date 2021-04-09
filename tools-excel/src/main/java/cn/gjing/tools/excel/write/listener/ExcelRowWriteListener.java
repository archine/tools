package cn.gjing.tools.excel.write.listener;

import cn.gjing.tools.excel.metadata.RowType;
import cn.gjing.tools.excel.metadata.listener.ExcelWriteListener;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * The Excel row writes out the listener
 * Can be used for additional extensions after this row is written out
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelRowWriteListener extends ExcelWriteListener {
    /**
     * All the cells for the current row are finish
     *
     * @param sheet   Current sheet
     * @param row     Current row
     * @param obj     Current Java object
     * @param index   Data indexing, depending on the row type, starts at 0
     * @param rowType Current row type
     */
    void completeRow(Sheet sheet, Row row, Object obj, int index, RowType rowType);

    /**
     * Before you create a row
     *
     * @param sheet   Current sheet
     * @param index   Data indexing, depending on the row type, starts at 0
     * @param rowType Current row type
     */
    default void createBefore(Sheet sheet, int index, RowType rowType) {

    }
}
