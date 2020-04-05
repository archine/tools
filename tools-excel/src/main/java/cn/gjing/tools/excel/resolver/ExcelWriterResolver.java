package cn.gjing.tools.excel.resolver;

import cn.gjing.tools.excel.BigTitle;
import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.MetaStyle;
import cn.gjing.tools.excel.listen.CustomWrite;
import org.apache.poi.ss.usermodel.Sheet;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Excel writer resolver
 *
 * @author Gjing
 **/
public interface ExcelWriterResolver {

    /**
     * Write excel big title
     *
     * @param sheet     Current sheet
     * @param metaStyle Excel meta style
     * @param bigTitle  Excel big title
     * @return this
     */
    ExcelWriterResolver writeTitle(BigTitle bigTitle, MetaStyle metaStyle, Sheet sheet);

    /**
     * Write excel body
     *
     * @param headFieldList Fields in Excel mapped entity that map to list headers
     * @param metaStyle     Excel meta style
     * @param sheet         Current sheet
     * @param data          data
     * @param needInit      Whether need init extension and width
     * @return this
     */
    ExcelWriterResolver write(List<?> data, Sheet sheet, List<Field> headFieldList, MetaStyle metaStyle, boolean needInit);

    /**
     * Write excel head
     *
     * @param headFieldList     Fields in Excel mapped entity that map to list headers
     * @param sheet             Current sheet
     * @param noContent         Have data
     * @param needHead          Is needHead excel entity or sheet?
     * @param excel             Excel annotation
     * @param metaStyle         Excel meta style
     * @param dropdownBoxValues Excel dropdown box values
     * @return this
     */
    ExcelWriterResolver writeHead(boolean noContent, List<Field> headFieldList, Sheet sheet, boolean needHead, MetaStyle metaStyle,
                                  Map<String, String[]> dropdownBoxValues, Excel excel);

    /**
     * Customize export excel head, body, big title, and so on
     *
     * @param processor Export processor
     */
    void customWrite(CustomWrite processor);

    /**
     * Output the contents of the cache
     *
     * @param fileName file name
     * @param response response
     */
    void flush(HttpServletResponse response, String fileName);
}
