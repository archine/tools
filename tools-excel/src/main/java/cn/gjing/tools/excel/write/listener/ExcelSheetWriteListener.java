package cn.gjing.tools.excel.write.listener;

import cn.gjing.tools.excel.write.ExcelWriterContext;

/**
 * Sheet lifecycle listener
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelSheetWriteListener extends ExcelWriteListener {
    /**
     * Has been created
     */
    void completeSheet(ExcelWriterContext context);
}
