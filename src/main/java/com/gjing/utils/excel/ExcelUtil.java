package com.gjing.utils.excel;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Archine
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
    public static void excelExport(HttpServletResponse response, List<Object[]> list, String[] headers, String title, @Nullable String info) {
        ExportExcel.generateHaveExcelName(response, list, headers, title, info);
    }
}
