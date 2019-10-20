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
     * 读Excel
     *
     * @param inputStream 输入流
     * @param excelClass  Excel对应的实体Class
     * @param listener    数据监听器
     * @param headerIndex  列表头下标
     * @param endIndex 读取截止位
     * @param sheetName   sheet名
     */
    void read(InputStream inputStream, Class<?> excelClass, Listener<List<Object>> listener, int headerIndex, int endIndex, String sheetName);
}
