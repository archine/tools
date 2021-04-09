package cn.gjing.tools.excel.metadata.resolver;

import cn.gjing.tools.excel.read.ExcelReaderContext;

/**
 * Excel reader resolver
 *
 * @author Gjing
 **/
public interface ExcelReaderResolver<R> {

    /**
     * Init resolver
     *
     * @param readerContext Excel reader global context
     */
    void init(ExcelReaderContext<R> readerContext);

    /**
     * Start Import excel
     *
     * @param headerIndex Excel really header start index
     * @param sheetName   sheetName
     */
    void read(int headerIndex, String sheetName);
}
