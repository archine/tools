package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.ExcelStyle;
import org.apache.poi.ss.usermodel.*;

/**
 * @author Gjing
 **/
class ExcelContentStyle implements ExcelStyle {
    @Override
    public CellStyle style(CellStyle cellStyle) {
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //自动换行
        cellStyle.setWrapText(true);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        return cellStyle;
    }
}
