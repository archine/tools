package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.ExcelStyle;
import org.apache.poi.ss.usermodel.*;

/**
 * @author Gjing
 **/
class ExcelHeaderStyle implements ExcelStyle {
    @Override
    public CellStyle style(CellStyle cellStyle) {
        cellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.index);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        return cellStyle;
    }
}
