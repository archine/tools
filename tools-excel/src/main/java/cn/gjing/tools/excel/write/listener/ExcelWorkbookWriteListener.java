package cn.gjing.tools.excel.write.listener;

import cn.gjing.tools.excel.metadata.listener.ExcelWriteListener;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * The Excel Workbook writes out the listener
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelWorkbookWriteListener extends ExcelWriteListener {
    /**
     * The data is written, but not flushed to an excel file
     *
     * @param workbook Current workbook
     */
    default void flushBefore(Workbook workbook){};

    /**
     * Has been created
     *
     * @param workbook Current created workbook
     */
    void completeWorkbook(Workbook workbook);
}
