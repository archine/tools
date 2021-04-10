package cn.gjing.tools.excel.write.listener;

import cn.gjing.tools.excel.metadata.listener.ExcelWriteListener;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * The Excel Sheet writes out the listener
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelSheetWriteListener extends ExcelWriteListener {
    /**
     * Has been created
     *
     * @param sheet Current created sheet
     */
    void completeSheet(Sheet sheet);
}
