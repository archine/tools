package cn.gjing.tools.excel.write.listener;

import cn.gjing.tools.excel.write.ExcelWriterContext;

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
     * @param context Excel write context
     */
    void completeSheet(ExcelWriterContext context);
}
