package cn.gjing.tools.excel.metadata;

import cn.gjing.tools.excel.BigTitle;
import cn.gjing.tools.excel.Excel;
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
     * @param sheet    Current sheet
     * @param bigTitle Excel big title
     * @return this
     */
    void writeTitle(BigTitle bigTitle, Sheet sheet);

    /**
     * Write excel body
     *
     * @param headFieldList Fields in Excel mapped entity that map to list headers
     * @param sheet         Current sheet
     * @param data          data
     * @return this
     */
    ExcelWriterResolver write(List<?> data, Sheet sheet, List<Field> headFieldList);

    /**
     * Write excel head
     *
     * @param headFieldList     Fields in Excel mapped entity that map to list headers
     * @param headNames         Excel head names
     * @param sheet             Current sheet
     * @param needHead          Is needHead excel entity or sheet?
     * @param excel             Excel annotation
     * @param dropdownBoxValues Excel dropdown box values
     * @return this
     */
    ExcelWriterResolver writeHead(List<Field> headFieldList, List<String> headNames, Sheet sheet, boolean needHead, Map<String, String[]> dropdownBoxValues, Excel excel);

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
