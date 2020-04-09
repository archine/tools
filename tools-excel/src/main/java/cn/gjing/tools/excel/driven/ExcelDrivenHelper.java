package cn.gjing.tools.excel.driven;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Gjing
 **/
@ControllerAdvice
class ExcelDrivenHelper {
    @ExceptionHandler(ExcelExport.class)
    public void doWrite(ExcelExport excelExport) {
        excelExport.getExcelWriter().flush();
    }

    @ExceptionHandler(ExcelImport.class)
    public void doRead(ExcelImport excelImport) {
        excelImport.getExcelReader()
                .read(excelImport.getExcelRead().headIndex(), excelImport.getExcelRead().sheet())
                .end();
    }
}
