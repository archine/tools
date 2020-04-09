package cn.gjing.tools.excel.metadata;

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
     * @param readerContext Excel reader context
     */
    void init(ExcelReaderContext<R> readerContext);

    /**
     * Import excel
     *
     * @param headerIndex Excel header index
     * @param sheetName   sheetName
     */
    void read(int headerIndex, String sheetName);
}
