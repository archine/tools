package cn.gjing.tools.excel.resolver;

import cn.gjing.tools.excel.listen.ReadCallback;
import cn.gjing.tools.excel.listen.ReadListener;

import java.io.InputStream;
import java.util.List;

/**
 * Excel reader resolver
 *
 * @author Gjing
 **/
public interface ExcelReaderResolver<R> {

    /**
     * Read excel
     *
     * @param inputStream Excel file inputStream
     * @param excelClass  Excel mapped entity
     * @param readListener  Excel import successful listener
     * @param headerIndex Excel header index
     * @param readLines   Read how many rows
     * @param sheetName   sheetName
     * @param callback    Excel import callback
     */
    void read(InputStream inputStream, Class<R> excelClass, ReadListener<List<R>> readListener, int headerIndex, int readLines, String sheetName, ReadCallback<R> callback);
}
