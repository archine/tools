package cn.gjing.tools.excel.metadata.listener;

import cn.gjing.tools.excel.metadata.ExcelFieldProperty;
import cn.gjing.tools.excel.metadata.annotation.ListenerNative;
import cn.gjing.tools.excel.metadata.aware.ExcelWriteContextAware;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.listener.ExcelStyleWriteListener;
import org.apache.poi.ss.usermodel.*;

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
    private final Map<Class<?>, Map<Integer, List<CellStyle>>> headStyleData;
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
    public void setHeadStyle(Row row, Cell cell, ExcelFieldProperty property, int index, int colIndex) {
        Map<Integer, List<CellStyle>> headStyle = this.headStyleData.computeIfAbsent(this.context.getExcelClass(), k -> new HashMap<>(32));
        List<CellStyle> cellStyleList = headStyle.get(colIndex);
        if (cellStyleList == null) {
            cellStyleList = new ArrayList<>();
            int colorLength = property.getColor().length;
            int fontColorLength = property.getFontColor().length;
            CellStyle cellStyle;
            int maxIndex = Math.max(colorLength, fontColorLength);
            for (int i = 0; i < maxIndex; i++) {
                cellStyle = this.context.getWorkbook().createCellStyle();
                cellStyle.setFillForegroundColor(property.getColor()[colorLength > i ? i : colorLength - 1].index);
                Font font = this.context.getWorkbook().createFont();
                font.setBold(true);
                font.setColor(property.getFontColor()[fontColorLength > i + 1 ? i : fontColorLength - 1].index);
                cellStyle.setFont(font);
                this.setColorAndBorder(cellStyle);
                this.setAlignment(cellStyle);
                cellStyleList.add(cellStyle);
            }
            headStyle.put(colIndex, cellStyleList);
        }
        if (index == 0) {
            this.setColumnWidth(property, colIndex);
            if (this.context.isTemplate()) {
                this.context.getSheet().setDefaultColumnStyle(colIndex, this.createDefaultStyle(property, colIndex));
            }
        }
        cell.setCellStyle(cellStyleList.size() > index ? cellStyleList.get(index) : cellStyleList.get(cellStyleList.size() - 1));
    }

    @Override
    public void setBodyStyle(Row row, Cell cell, ExcelFieldProperty property, int index, int colIndex) {
        if (!this.context.isExistHead() && index == 0) {
            this.setColumnWidth(property, colIndex);
        }
        cell.setCellStyle(this.createDefaultStyle(property, colIndex));
    }

    private CellStyle createDefaultStyle(ExcelFieldProperty property, int colIndex) {
        CellStyle cellStyle = this.defaultColumnStyle.get(property.getFormat());
        if (cellStyle == null) {
            cellStyle = this.context.getWorkbook().createCellStyle();
            this.setAlignment(cellStyle);
            if (!property.getFormat().isEmpty()) {
                cellStyle.setDataFormat(this.context.getWorkbook().createDataFormat().getFormat(property.getFormat()));
            }
            this.defaultColumnStyle.put(property.getFormat(), cellStyle);
        }
        return cellStyle;
    }

    private void setColumnWidth(ExcelFieldProperty property, int colIndex) {
        int defaultColumnWidth = this.context.getSheet().getColumnWidth(colIndex);
        if (property.getWidth() > defaultColumnWidth) {
            this.context.getSheet().setColumnWidth(colIndex, property.getWidth());
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
