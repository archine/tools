package cn.gjing.tools.excel.resolver;

import cn.gjing.tools.excel.Listener;

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
     * @param listener    Result listener
     * @param headerIndex Excel header index
     * @param readLines   Read how many rows
     * @param sheetName   sheetName
     */
    void read(InputStream inputStream, Class<R> excelClass, Listener<List<R>> listener, int headerIndex, int readLines, String sheetName);
}
