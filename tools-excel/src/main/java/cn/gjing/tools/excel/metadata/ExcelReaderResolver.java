package cn.gjing.tools.excel.metadata;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Excel reader resolver
 *
 * @author Gjing
 **/
public interface ExcelReaderResolver<R> {

    /**
     * Import excel
     *
     * @param excelClass     Excel mapped entity
     * @param headerIndex    Excel header index
     * @param sheetName      sheetName
     * @param excelFieldList Excel field list
     * @param collect        Whether collect the Java objects generated for each row when imported
     */
    void read(Class<R> excelClass, int headerIndex, String sheetName, List<Field> excelFieldList, boolean collect);
}
