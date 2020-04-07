package cn.gjing.tools.excel.write.style;

import cn.gjing.tools.excel.ExcelField;
import org.apache.poi.ss.usermodel.*;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Gjing
 **/
public class DefaultExcelStyle implements BaseExcelStyleListener {
    private Workbook workbook;
    private CellStyle titleStyle;
    private Map<String, CellStyle> headStyle;
    private Map<String, CellStyle> bodyStyle;

    @Override
    public void init(Workbook workbook) {
        this.workbook = workbook;
    }

    @Override
    public void setTitleStyle(Cell cell) {
        if (this.titleStyle == null) {
            CellStyle cellStyle = this.workbook.createCellStyle();
            cellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setWrapText(true);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            this.titleStyle = cellStyle;
        }
        cell.setCellStyle(this.titleStyle);
    }

    @Override
    public void setHeadStyle(Row row, Cell cell, ExcelField excelField, Field field, String headName, int rowIndex, int colIndex) {
        CellStyle cellStyle = this.headStyle.get(headName);
        if (cellStyle == null) {
            cellStyle = this.workbook.createCellStyle();
            cellStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.index);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setWrapText(true);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.index);
            cellStyle.setFont(font);
            this.headStyle.put(headName, cellStyle);
        }
        cell.setCellStyle(cellStyle);
    }

    @Override
    public void setBodyStyle(Row row, Cell cell, ExcelField excelField, Field field, String headName, int rowIndex, int colIndex) {
        CellStyle cellStyle = this.bodyStyle.get(headName);
        if (cellStyle == null) {
            cellStyle = this.workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setWrapText(true);
            cellStyle.setLocked(false);
            if (!"".equals(excelField.format())) {
                cellStyle.setDataFormat(this.workbook.createDataFormat().getFormat(excelField.format()));
            }
            this.bodyStyle.put(headName, cellStyle);
        }
        cell.setCellStyle(cellStyle);
    }

    @Override
    public void createdCell(Sheet sheet, Row row, Cell cell, ExcelField excelField, Field field, String headName, int rowIndex, int colIndex, boolean isHead, Object value) {
        if (isHead) {
            this.setHeadStyle(row, cell, excelField, field, headName, rowIndex, colIndex);
            return;
        }
        this.setBodyStyle(row, cell, excelField, field, headName, rowIndex, colIndex);
    }
}
