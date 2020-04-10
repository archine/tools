package cn.gjing.tools.excel.write.listener;

import cn.gjing.tools.excel.write.ExcelWriterContext;

/**
 * Sheet lifecycle listener
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelWorkbookWriteListener extends ExcelWriteListener {
    /**
     * The data is written, but not flushed to an excel file
     *
     * @param context Excel write context
     */
    void flushBefore(ExcelWriterContext context);
}
