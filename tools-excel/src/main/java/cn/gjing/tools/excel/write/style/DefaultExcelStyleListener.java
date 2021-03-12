package cn.gjing.tools.excel.write.style;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.metadata.ExcelColor;
import cn.gjing.tools.excel.metadata.RowType;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.listener.ExcelCellWriteListener;
import lombok.Getter;
import org.apache.poi.ss.usermodel.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Excel write type is a style listener of the binding type
 *
 * @author Gjing
 **/
public class DefaultExcelStyleListener implements ExcelStyleWriteListener, ExcelCellWriteListener {
    private Workbook workbook;
    @Getter
    private Map<Integer, CellStyle> titleStyles;
    @Getter
    private Map<Integer, List<CellStyle>> headStyle;
    @Getter
    private Map<Integer, CellStyle> bodyStyle;

    @Override
    public void init(Workbook workbook) {
        this.workbook = workbook;
        this.headStyle = new HashMap<>(32);
        this.bodyStyle = new HashMap<>(32);
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
        List<CellStyle> cellStyleList = this.headStyle.get(colIndex);
        if (cellStyleList == null) {
            cellStyleList = new ArrayList<>();
            CellStyle cellStyle;
            if (excelField == null) {
                cellStyle = this.workbook.createCellStyle();
                cellStyle.setFillForegroundColor(ExcelColor.LIME.index);
                Font font = workbook.createFont();
                font.setBold(true);
                font.setColor(ExcelColor.WHITE.index);
                cellStyle.setFont(font);
                this.setColorAndBorder(cellStyle);
                this.setAlignment(cellStyle);
                cellStyleList.add(cellStyle);
            } else {
                int maxIndex = Math.max(excelField.color().length, excelField.fontColor().length);
                for (int i = 0; i < maxIndex; i++) {
                    cellStyle = this.workbook.createCellStyle();
                    cellStyle.setFillForegroundColor(excelField.color()[excelField.color().length > index + 1 ? index : excelField.color().length - 1].index);
                    Font font = workbook.createFont();
                    font.setBold(true);
                    font.setColor(excelField.fontColor()[excelField.fontColor().length > index + 1 ? index : excelField.fontColor().length - 1].index);
                    cellStyle.setFont(font);
                    this.setColorAndBorder(cellStyle);
                    this.setAlignment(cellStyle);
                    cellStyleList.add(cellStyle);
                }
            }
        }
        cell.setCellStyle(index + 1 == cellStyleList.size() ? cellStyleList.get(index) : cellStyleList.get(cellStyleList.size() - 1));
    }

    @Override
    public void setBodyStyle(Row row, Cell cell, ExcelField excelField, Field field, int index, int colIndex) {
        CellStyle cellStyle = this.bodyStyle.get(colIndex);
        if (cellStyle == null) {
            cellStyle = this.workbook.createCellStyle();
            this.setAlignment(cellStyle);
            String format = excelField == null ? "" : excelField.format();
            if (!"".equals(format)) {
                cellStyle.setDataFormat(this.workbook.createDataFormat().getFormat(format));
            }
            this.bodyStyle.put(colIndex, cellStyle);
        }
        cell.setCellStyle(cellStyle);
    }

    @Override
    public void completeCell(Sheet sheet, Row row, Cell cell, ExcelField excelField, Field field, int index,
                             int colIndex, RowType rowType) {
        if (rowType == RowType.HEAD) {
            if (index == 0) {
                CellStyle cellStyle = this.workbook.createCellStyle();
                this.setAlignment(cellStyle);
                String format = excelField == null ? "" : excelField.format();
                if (!"".equals(format)) {
                    cellStyle.setDataFormat(this.workbook.createDataFormat().getFormat(format));
                }
                sheet.setDefaultColumnStyle(colIndex, cellStyle);
                sheet.setColumnWidth(colIndex, excelField == null ? 5120 : excelField.width());
            }
            this.setHeadStyle(row, cell, excelField, field, index, colIndex);
            return;
        }
        this.setBodyStyle(row, cell, excelField, field, index, colIndex);
    }

    private void setAlignment(CellStyle cellStyle) {
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
    }

    private void setColorAndBorder(CellStyle cellStyle) {
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.index);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.GREY_40_PERCENT.index);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.GREY_40_PERCENT.index);
    }
}
