package cn.gjing.tools.excel.metadata.listener;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.metadata.ExcelColor;
import cn.gjing.tools.excel.metadata.RowType;
import cn.gjing.tools.excel.metadata.annotation.ListenerNative;
import cn.gjing.tools.excel.metadata.aware.ExcelWorkbookAware;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.listener.ExcelRowWriteListener;
import cn.gjing.tools.excel.write.listener.ExcelSheetWriteListener;
import cn.gjing.tools.excel.write.listener.ExcelStyleWriteListener;
import org.apache.poi.ss.usermodel.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default style listener
 * which can be turned off when a writer is created using the initDefaultStyle parameter
 *
 * @author Gjing
 **/
@ListenerNative
public class DefaultExcelStyleListener implements ExcelStyleWriteListener, ExcelSheetWriteListener, ExcelWorkbookAware, ExcelRowWriteListener {
    private Workbook workbook;
    private Sheet currentSheet;
    private final Map<Integer, CellStyle> titleStyles;
    private final Map<Integer, List<CellStyle>> headStyle;
    private final Map<String, CellStyle> defaultColumnStyle;
    private boolean set = true;

    public DefaultExcelStyleListener() {
        this.headStyle = new HashMap<>(32);
        this.defaultColumnStyle = new HashMap<>(16);
        this.titleStyles = new HashMap<>(16);
    }

    @Override
    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    @Override
    public void completeSheet(Sheet sheet) {
        this.currentSheet = sheet;
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
                    cellStyle.setFillForegroundColor(excelField.color()[excelField.color().length > i ? i : excelField.color().length - 1].index);
                    Font font = workbook.createFont();
                    font.setBold(true);
                    font.setColor(excelField.fontColor()[excelField.fontColor().length > i + 1 ? i : excelField.fontColor().length - 1].index);
                    cellStyle.setFont(font);
                    this.setColorAndBorder(cellStyle);
                    this.setAlignment(cellStyle);
                    cellStyleList.add(cellStyle);
                }
            }
            this.headStyle.put(colIndex, cellStyleList);
        }
        if (this.set) {
            boolean isSimpleType = excelField == null;
            this.setColumnWidth(excelField, colIndex, isSimpleType);
            this.currentSheet.setDefaultColumnStyle(colIndex, this.createDefaultStyle(excelField, isSimpleType));
        }
        cell.setCellStyle(cellStyleList.size() > index ? cellStyleList.get(index) : cellStyleList.get(cellStyleList.size() - 1));
    }

    @Override
    public void setBodyStyle(Row row, Cell cell, ExcelField excelField, Field field, int index, int colIndex) {
        boolean isSimpleType = excelField == null;
        if (this.set) {
            this.setColumnWidth(excelField, colIndex, isSimpleType);
        }
        cell.setCellStyle(this.createDefaultStyle(excelField, isSimpleType));
    }

    @Override
    public void completeRow(Sheet sheet, Row row, Object obj, int index, RowType rowType) {
        if (index == 0) {
            this.set = false;
        }
    }

    private CellStyle createDefaultStyle(ExcelField excelField, boolean isSimpleType) {
        String format = isSimpleType ? "" : excelField.format();
        CellStyle cellStyle = this.defaultColumnStyle.get(format);
        if (cellStyle == null) {
            cellStyle = this.workbook.createCellStyle();
            this.setAlignment(cellStyle);
            if (!format.isEmpty()) {
                cellStyle.setDataFormat(this.workbook.createDataFormat().getFormat(format));
            }
            this.defaultColumnStyle.put(format, cellStyle);
        }
        return cellStyle;
    }

    private void setColumnWidth(ExcelField excelField, int colIndex, boolean isSimpleType) {
        int defaultColumnWidth = this.currentSheet.getColumnWidth(colIndex);
        int newColumnWidth = isSimpleType ? 5120 : excelField.width();
        if (defaultColumnWidth != newColumnWidth) {
            this.currentSheet.setColumnWidth(colIndex, newColumnWidth);
        }
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
