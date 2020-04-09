package cn.gjing.tools.excel.driven;

import cn.gjing.tools.excel.write.resolver.ExcelWriter;
import lombok.Getter;

/**
 * @author Gjing
 **/
class ExcelExport extends RuntimeException{
    @Getter
    private ExcelWriter excelWriter;
    public ExcelExport(ExcelWriter excelWriter) {
        this.excelWriter = excelWriter;
    }
}
