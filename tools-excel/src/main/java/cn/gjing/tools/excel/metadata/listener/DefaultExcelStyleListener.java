package cn.gjing.tools.excel.metadata.listener;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.metadata.ExcelColor;
import cn.gjing.tools.excel.metadata.annotation.ListenerNative;
import cn.gjing.tools.excel.metadata.aware.ExcelWriteContextAware;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.ExcelWriterContext;
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
public class DefaultExcelStyleListener implements ExcelStyleWriteListener, ExcelWriteContextAware {
    private ExcelWriterContext context;
    private final Map<Integer, CellStyle> titleStyles;
    private final Map<Class<?>, Map<String, List<CellStyle>>> headStyleData;
    private final Map<String, CellStyle> defaultColumnStyle;

    public DefaultExcelStyleListener() {
        this.headStyleData = new HashMap<>(16);
        this.defaultColumnStyle = new HashMap<>(16);
        this.titleStyles = new HashMap<>(16);
    }

    @Override
    public void setContext(ExcelWriterContext writerContext) {
        this.context = writerContext;
    }

    @Override
    public void setTitleStyle(BigTitle bigTitle, Cell cell) {
        CellStyle titleStyle = titleStyles.get(bigTitle.getIndex());
        if (titleStyle == null) {
            titleStyle = this.context.getWorkbook().createCellStyle();
            titleStyle.setFillForegroundColor(bigTitle.getColor().index);
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(bigTitle.getAlignment());
            titleStyle.setWrapText(true);
            Font font = this.context.getWorkbook().createFont();
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
        Map<String, List<CellStyle>> headStyle = this.headStyleData.computeIfAbsent(this.context.getExcelClass(), k -> new HashMap<>(16));
        List<CellStyle> cellStyleList = headStyle.get(field.getName());
        boolean isSimpleType = excelField == null;
        if (cellStyleList == null) {
            cellStyleList = new ArrayList<>();
            ExcelColor[] color = isSimpleType ? this.context.getPropSupplier().getHeadColor(colIndex) : excelField.color();
            ExcelColor[] fontColor  = isSimpleType ? this.context.getPropSupplier().getHeadFontColor(colIndex) : excelField.fontColor();
            CellStyle cellStyle;
            int maxIndex = Math.max(color.length, fontColor.length);
            for (int i = 0; i < maxIndex; i++) {
                cellStyle = this.context.getWorkbook().createCellStyle();
                cellStyle.setFillForegroundColor(color[color.length > i ? i : color.length - 1].index);
                Font font = this.context.getWorkbook().createFont();
                font.setBold(true);
                font.setColor(fontColor[fontColor.length > i + 1 ? i : fontColor.length - 1].index);
                cellStyle.setFont(font);
                this.setColorAndBorder(cellStyle);
                this.setAlignment(cellStyle);
                cellStyleList.add(cellStyle);
            }
            headStyle.put(field.getName(), cellStyleList);
        }
        if (index == 0) {
            this.setColumnWidth(excelField, colIndex, isSimpleType);
            if (this.context.isTemplate()) {
                this.context.getSheet().setDefaultColumnStyle(colIndex, this.createDefaultStyle(excelField, isSimpleType, colIndex));
            }
        }
        cell.setCellStyle(cellStyleList.size() > index ? cellStyleList.get(index) : cellStyleList.get(cellStyleList.size() - 1));
    }

    @Override
    public void setBodyStyle(Row row, Cell cell, ExcelField excelField, Field field, int index, int colIndex) {
        boolean isSimpleType = excelField == null;
        if (!this.context.isExistHead() && index == 0) {
            this.setColumnWidth(excelField, colIndex, isSimpleType);
        }
        cell.setCellStyle(this.createDefaultStyle(excelField, isSimpleType, colIndex));
    }

    private CellStyle createDefaultStyle(ExcelField excelField, boolean isSimpleType, int colIndex) {
        String format = isSimpleType ? this.context.getPropSupplier().getFormat(colIndex) : excelField.format();
        CellStyle cellStyle = this.defaultColumnStyle.get(format);
        if (cellStyle == null) {
            cellStyle = this.context.getWorkbook().createCellStyle();
            this.setAlignment(cellStyle);
            if (!format.isEmpty()) {
                cellStyle.setDataFormat(this.context.getWorkbook().createDataFormat().getFormat(format));
            }
            this.defaultColumnStyle.put(format, cellStyle);
        }
        return cellStyle;
    }

    private void setColumnWidth(ExcelField excelField, int colIndex, boolean isSimpleType) {
        int defaultColumnWidth = this.context.getSheet().getColumnWidth(colIndex);
        int newColumnWidth = isSimpleType ? this.context.getPropSupplier().getColWidth(colIndex) : excelField.width();
        if (newColumnWidth > defaultColumnWidth) {
            this.context.getSheet().setColumnWidth(colIndex, newColumnWidth);
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
