package cn.gjing.tools.excel.resolver;

import cn.gjing.tools.excel.BigTitle;
import cn.gjing.tools.excel.MetaStyle;
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
     * 写Excel
     *
     * @param headFieldList 列表头字段
     * @param sheetName     sheet名称
     * @param workbook      workbook 工作簿
     * @param bigTitle      大标题
     * @param metaStyle    excel样式
     * @param data          要导出的数据
     */
    void write(List<?> data, Workbook workbook, String sheetName, List<Field> headFieldList, MetaStyle metaStyle, BigTitle bigTitle);

    /**
     * 输出所有内容
     *
     * @param fileName 文件名
     * @param response 响应
     */
    void flush(HttpServletResponse response, String fileName);
}
