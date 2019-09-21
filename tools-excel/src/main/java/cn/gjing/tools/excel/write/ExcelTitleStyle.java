package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.ExcelStyle;
import org.apache.poi.ss.usermodel.*;

/**
 * @author Gjing
 **/
class ExcelTitleStyle implements ExcelStyle {
    @Override
    public CellStyle style(CellStyle cellStyle) {
        cellStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }
}
