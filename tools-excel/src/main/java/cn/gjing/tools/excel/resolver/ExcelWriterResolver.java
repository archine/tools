package cn.gjing.tools.excel.resolver;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.MetaObject;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Excel writer resolver
 *
 * @author Gjing
 **/
public interface ExcelWriterResolver {

    /**
     * Write excel
     *
     * @param headFieldList Fields in Excel mapped entity that map to list headers
     * @param sheetName     sheet name
     * @param workbook      workbook
     * @param data          data
     * @param metaObject    Excel meta object
     * @param changed Is changed excel entity or sheet?
     * @param excel Excel annotation
     */
    void write(List<?> data, Workbook workbook, String sheetName, List<Field> headFieldList, MetaObject metaObject, boolean changed, Excel excel);

    /**
     * Output the contents of the cache
     *
     * @param fileName file name
     * @param response response
     */
    void flush(HttpServletResponse response, String fileName);
}
