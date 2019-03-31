package com.gjing.utils.excel;

import com.gjing.annotation.ExcludeParam;
import com.gjing.annotation.NotNull2;

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
     * @param list     Data that needs to be exported
     * @param headers  excel headers
     * @param title    Excel file name
     * @param info     Excel introduction, Pass null or "" if you don't need it
     */
    @NotNull2
    public static void excelExport(HttpServletResponse response, @ExcludeParam List<Object[]> list, String[] headers, String title, @ExcludeParam String info) {
        ExportExcel.generateHaveExcelName(response, list, headers, title, info);
    }
}
