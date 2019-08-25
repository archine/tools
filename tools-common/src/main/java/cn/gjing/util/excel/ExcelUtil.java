package cn.gjing.util.excel;

import cn.gjing.annotation.ExcludeParam;
import cn.gjing.annotation.NotNull;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Gjing
 **/
public class ExcelUtil {

    /**
     * excel导出
     *
     * @param response response
     * @param data     需要导出的数据
     * @param headers  excel列表头
     * @param fileName 文件名
     * @param description  描述
     * @param cellAddresses 设置描述的单元格样式
     */
    @NotNull
    public static void excelExport(HttpServletResponse response, @ExcludeParam List<Object[]> data, String[] headers,
                                   String fileName, String description, CellRangeAddress cellAddresses) {
        ExportExcel.of(data, headers, fileName, description,cellAddresses).generateHaveExcelName(response);
    }

    /**
     * excel导出
     * @param response response
     * @param data 数据
     * @param headers 列表头
     * @param fileName 文件名
     */
    @NotNull
    public static void excelExport(HttpServletResponse response, @ExcludeParam List<Object[]> data, String[] headers,
                                   String fileName) {
        ExportExcel.of(data, headers, fileName, null, null).generateHaveExcelName(response);
    }
}
