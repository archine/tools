package cn.gjing.tools.excel.write.listener;

import cn.gjing.tools.excel.write.ExcelWriterContext;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Sheet lifecycle listener
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelSheetWriteListener extends ExcelWriteListener {
    /**
     * Has been created
     *
     * @param sheet   Current created sheet
     * @param context Excel write context
     */
    void completeSheet(Sheet sheet, ExcelWriterContext context);
}
