package cn.gjing.tools.excel.write.listener;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * Sheet lifecycle listener
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface WorkbookWriteListener extends WriteListener {
    /**
     * The data is written, but not flushed to an excel file
     *
     * @param workbook Current workbook
     * @param fileName Excel file name
     */
    void flushBefore(Workbook workbook, String fileName);
}
