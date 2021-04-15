package cn.gjing.tools.excel.metadata.aware;

import cn.gjing.tools.excel.read.ExcelReaderContext;

/**
 * Retrieve excel reader context
 *
 * @author Gjing
 **/
public interface ExcelReaderContextAware<R> extends ExcelAware {
    /**
     * Set excel reader context
     *
     * @param readerContext Excel reader context
     */
    void setContext(ExcelReaderContext<R> readerContext);
}
