package cn.gjing.tools.excel.metadata;

/**
 * Excel reader resolver
 *
 * @author Gjing
 **/
public interface ExcelReaderResolver<R> {
    /**
     * Import excel
     *
     * @param headerIndex    Excel header index
     * @param sheetName      sheetName
     */
    void read(int headerIndex, String sheetName);
}
