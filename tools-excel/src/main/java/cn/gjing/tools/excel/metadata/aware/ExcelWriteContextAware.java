package cn.gjing.tools.excel.metadata.aware;

import cn.gjing.tools.excel.write.ExcelWriterContext;

/**
 * Retrieve excel writer context
 *
 * @author Gjing
 **/
public interface ExcelWriteContextAware extends ExcelAware {
    /**
     * Set excel writer context
     *
     * @param writerContext Excel writer context
     */
    void setContext(ExcelWriterContext writerContext);
}
