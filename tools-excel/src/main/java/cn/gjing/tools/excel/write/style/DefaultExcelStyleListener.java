package cn.gjing.tools.excel.write.style;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.metadata.ExcelColor;
import cn.gjing.tools.excel.write.BigTitle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * The Excel write type is a style listener of the binding type
 *
 * @author Gjing
 **/
public final class DefaultExcelStyleListener implements ExcelStyleWriteListener {
    private Workbook workbook;
    private Map<Integer, CellStyle> titleStyles;
    private Map<Integer, CellStyle> headStyle;
    private Map<Integer, CellStyle> bodyStyle;

    @Override
    public void init(Workbook workbook) {
        this.workbook = workbook;
        this.headStyle = new HashMap<>(16);
        this.bodyStyle = new HashMap<>(16);
        this.titleStyles = new HashMap<>(8);
    }

    @Override
    public void setTitleStyle(BigTitle bigTitle, Cell cell) {
        CellStyle titleStyle = titleStyles.get(bigTitle.getIndex());
        if (titleStyle == null) {
            titleStyle = this.workbook.createCellStyle();
            titleStyle.setFillForegroundColor(bigTitle.getColor().index);
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(bigTitle.getAlignment());
            titleStyle.setWrapText(true);
            Font font = this.workbook.createFont();
            font.setColor(bigTitle.getFontColor().index);
            font.setBold(bigTitle.isBold());
            font.setFontHeight(bigTitle.getFontHeight());
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            titleStyle.setFont(font);
            this.titleStyles.put(bigTitle.getIndex(), titleStyle);
        }
        cell.setCellStyle(titleStyle);
    }

    @Override
    public void setHeadStyle(Row row, Cell cell, ExcelField excelField, Field field, int index, int colIndex) {
        CellStyle cellStyle = this.headStyle.get(colIndex);
        if (cellStyle == null) {
            cellStyle = this.workbook.createCellStyle();
            cellStyle.setFillForegroundColor(excelField == null ? ExcelColor.LIME.index : excelField.color().index);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setWrapText(true);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setLeftBorderColor(IndexedColors.GREY_40_PERCENT.index);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setRightBorderColor(IndexedColors.GREY_40_PERCENT.index);
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(excelField == null ? ExcelColor.WHITE.index : excelField.fontColor().index);
            cellStyle.setFont(font);
            this.headStyle.put(colIndex, cellStyle);
        }
        cell.setCellStyle(cellStyle);
    }

    @Override
    public void setBodyStyle(Row row, Cell cell, ExcelField excelField, Field field, int index, int colIndex) {
        CellStyle cellStyle = this.bodyStyle.get(colIndex);
        if (cellStyle == null) {
            cellStyle = this.workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setWrapText(true);
            cellStyle.setDataFormat(this.workbook.createDataFormat().getFormat(excelField == null ? "" : excelField.format()));
            this.bodyStyle.put(colIndex, cellStyle);
        }
        cell.setCellStyle(cellStyle);
    }

    @Override
    public void completeCell(Sheet sheet, Row row, Cell cell, ExcelField excelField, Field field, int index,
                             int colIndex, boolean isHead) {
        if (isHead) {
            if (index == 0) {
                CellStyle cellStyle = this.workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                cellStyle.setWrapText(true);
                cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(excelField == null ? "" : excelField.format()));
                sheet.setDefaultColumnStyle(colIndex, cellStyle);
                sheet.setColumnWidth(colIndex, excelField == null ? 5120 : excelField.width());
            }
            this.setHeadStyle(row, cell, excelField, field, index, colIndex);
            return;
        }
        this.setBodyStyle(row, cell, excelField, field, index, colIndex);
    }

    @Override
    public void completeRow(Sheet sheet, Row row, Object obj, int index, boolean isHead) {
        if (isHead) {
            row.setHeight((short) 370);
        }
    }
}
