package cn.gjing.tools.excel;

import org.apache.poi.ss.usermodel.*;

/**
 * Excel style
 *
 * @author Gjing
 **/
public interface ExcelStyle {

    /**
     * Big title style
     *
     * @param workbook workbook
     * @param cellStyle cellStyle
     * @return CellStyle
     */
    default CellStyle setTitleStyle(Workbook workbook, CellStyle cellStyle) {
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        return cellStyle;
    }

    /**
     * Header style
     *
     * @param workbook workbook
     * @param cellStyle cellStyle
     * @return CellStyle
     */
    default CellStyle setHeaderStyle(Workbook workbook, CellStyle cellStyle) {
        cellStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.index);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.index);
        cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * Body style
     *
     * @param workbook workbook
     * @param cellStyle cellStyle
     * @return CellStyle
     */
    default CellStyle setBodyStyle(Workbook workbook, CellStyle cellStyle) {
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
        cellStyle.setLocked(false);
        return cellStyle;
    }
}
