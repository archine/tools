package cn.gjing.tools.excel.driven;

import cn.gjing.tools.excel.read.resolver.ExcelReader;
import lombok.Getter;

/**
 * @author Gjing
 **/
@SuppressWarnings("rawtypes")
class ExcelImport extends RuntimeException {
    @Getter
    private ExcelRead excelRead;
    @Getter
    private ExcelReader excelReader;

    public ExcelImport(ExcelRead excelRead, ExcelReader excelReader) {
        this.excelRead = excelRead;
        this.excelReader = excelReader;
    }
}
