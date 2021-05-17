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
     * The data is all written to the Excel file stream and the file download is about to begin
     *
     * @param workbook Current workbook
     * @return If true, the download will start
     */
    boolean flushBefore(Workbook workbook);
}
