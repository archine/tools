package cn.gjing.excel;

import cn.gjing.annotation.ExcludeParam;
import cn.gjing.annotation.NotNull;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Gjing
 **/
public class ExcelUtil {

    /**
     * Excel export
     *
     * @param response response
     * @param data     Data that needs to be exported
     * @param headers  excel headers
     * @param fileName 文件名
     * @param outline  概要
     */
    @NotNull
    public static void excelExport(HttpServletResponse response, @ExcludeParam List<Object[]> data, String[] headers,
                                   String fileName, @ExcludeParam String outline) {
        ExportExcel.of(data, headers, fileName, outline).generateHaveExcelName(response);
    }
}
