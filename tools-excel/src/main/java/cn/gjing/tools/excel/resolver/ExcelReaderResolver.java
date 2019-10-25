package cn.gjing.tools.excel.resolver;


import cn.gjing.tools.excel.Listener;

import java.io.InputStream;
import java.util.List;

/**
 * Excel reader resolver
 *
 * @author Gjing
 **/
public interface ExcelReaderResolver {

    /**
     * Read excel
     *
     * @param inputStream Excel file inputStream
     * @param excelClass  Excel mapped entity
     * @param listener    Result listener
     * @param headerIndex Excel header index
     * @param endIndex    Read the cutoff index
     * @param sheetName   sheetName
     */
    void read(InputStream inputStream, Class<?> excelClass, Listener<List<Object>> listener, int headerIndex, int endIndex, String sheetName);
}
