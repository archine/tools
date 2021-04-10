package cn.gjing.tools.excel.write.listener;

import cn.gjing.tools.excel.metadata.listener.ExcelWriteListener;
import cn.gjing.tools.excel.write.ExcelWriterContext;

/**
 * Write out the context listener
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelWriteContextListener extends ExcelWriteListener {
    /**
     * Writer context  has initialized
     *
     * @param writerContext Write context
     */
    void setContext(ExcelWriterContext writerContext);
}
