package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.*;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.valid.DateValid;
import cn.gjing.tools.excel.valid.ExplicitValid;
import cn.gjing.tools.excel.valid.NumericValid;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Gjing
 **/
class ExcelHelper {
    private Workbook workbook;
    private Map<String, SimpleDateFormat> formatMap;
    private Map<Integer, String> formulaMap;
    private Map<String, MetaStyle> customerMetaStyleMap;

    public ExcelHelper(Workbook workbook) {
        this.workbook = workbook;
        this.customerMetaStyleMap = new HashMap<>(16);
    }

    public int setBigTitle(List<Field> headFieldList, MetaObject metaObject, Sheet sheet) {
        if (metaObject.getBigTitle() != null) {
            Row row;
            Cell cell;
            int offset = sheet.getPhysicalNumberOfRows();
            int titleOffset = offset + metaObject.getBigTitle().getLines() - 1;
            sheet.addMergedRegion(new CellRangeAddress(offset, titleOffset, 0, headFieldList.size() - 1));
            for (int i = 0; i < metaObject.getBigTitle().getLines(); i++) {
                row = sheet.createRow(offset + i);
                for (int j = 0; j < headFieldList.size(); j++) {
                    cell = row.createCell(j);
                    cell.setCellValue(metaObject.getBigTitle().getContent());
                    cell.setCellStyle(metaObject.getMetaStyle().getTitleStyle());
                }
            }
            return titleOffset;
        } else {
            return sheet.getLastRowNum();
        }
    }

    public void setVal(List<?> data, List<Field> headFieldList, Sheet sheet, boolean changed, int rowIndex, MetaObject metaObject) {
        boolean locked = false;
        if (changed) {
            rowIndex = rowIndex == 0 ? 0 : rowIndex + 1;
            Row headRow = sheet.createRow(rowIndex);
            MetaStyle metaStyle;
            ExcelStyle excelStyle;
            for (int i = 0; i < headFieldList.size(); i++) {
                Cell headCell = headRow.createCell(i);
                Field field = headFieldList.get(i);
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                if (excelField.style() == DefaultExcelStyle.class) {
                    headCell.setCellStyle(metaObject.getMetaStyle().getHeadStyle());
                    this.customerMetaStyleMap.put(field.getName(), metaObject.getMetaStyle());
                } else {
                    metaStyle = this.customerMetaStyleMap.get(field.getName());
                    if (metaStyle == null) {
                        try {
                            excelStyle = excelField.style().newInstance();
                            metaStyle = new MetaStyle();
                            metaStyle.setBodyStyle(excelStyle.setBodyStyle(this.workbook.createCellStyle()));
                            CellStyle headerStyle = excelStyle.setHeaderStyle(this.workbook.createCellStyle());
                            metaStyle.setHeadStyle(headerStyle);
                            this.customerMetaStyleMap.put(field.getName(), metaStyle);
                            headCell.setCellStyle(headerStyle);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        headCell.setCellStyle(metaStyle.getHeadStyle());
                    }
                }
                headCell.setCellValue(excelField.value());
                sheet.setColumnWidth(i, excelField.width());
                if (data == null || data.isEmpty()) {
                    locked = this.addValid(field, headRow, i, locked, sheet, metaObject);
                }
            }
        }
        if (data == null || data.isEmpty()) {
            return;
        }
        rowIndex++;
        Map<Object, ExcelModel> excelModelMap = new HashMap<>(16);
        for (int i = 0, dataSize = data.size(); i < dataSize; i++) {
            Object o = data.get(i);
            Row valueRow = sheet.createRow(rowIndex + i);
            for (int j = 0, headSize = headFieldList.size(); j < headSize; j++) {
                Field field = headFieldList.get(j);
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                field.setAccessible(true);
                Object value = BeanUtils.getFieldValue(o, field);
                Cell valueCell = valueRow.createCell(j);
                if (i == 0) {
                    if (excelField.sum().open()) {
                        if (formulaMap == null) {
                            this.formulaMap = new HashMap<>(16);
                        }
                        this.formulaMap.put(j, valueCell.getAddress().formatAsString() + ":");
                    }
                    if (excelField.autoMerge()) {
                        this.putExcelModel(valueRow, value, excelModelMap, i + "-" + j);
                    }
                } else {
                    if (excelField.autoMerge()) {
                        String mergeKey = i + "-" + j;
                        String oldKey = (i - 1) + "-" + j;
                        ExcelModel excelModel = excelModelMap.get(oldKey);
                        if (excelModel != null) {
                            if (ParamUtils.equals(value, excelModel.getOldValue())) {
                                if (i == dataSize - 1) {
                                    sheet.addMergedRegion(new CellRangeAddress(excelModel.getRowIndex(), valueRow.getRowNum(), j, j));
                                } else {
                                    excelModelMap.put(mergeKey, excelModel);
                                }
                            } else {
                                if (excelModel.getRowIndex() + 1 < valueRow.getRowNum()) {
                                    sheet.addMergedRegion(new CellRangeAddress(excelModel.getRowIndex(), valueRow.getRowNum() - 1, j, j));
                                }
                                if (i != dataSize - 1) {
                                    this.putExcelModel(valueRow, value, excelModelMap, mergeKey);
                                }
                            }
                        }
                    }
                    if (i == dataSize - 1) {
                        if (excelField.sum().open()) {
                            String formula = formulaMap.get(j) + valueCell.getAddress().formatAsString();
                            this.sum(sheet, j, valueCell.getAddress().getRow() + 1, formula, excelField.sum());
                        }
                    }
                }
                this.setCellVal(excelField, field, valueCell, value);
            }
        }
    }

    private void putExcelModel(Row row, Object value, Map<Object, ExcelModel> excelModelMap, String key) {
        excelModelMap.put(key, ExcelModel.builder()
                .oldValue(value)
                .rowIndex(row.getRowNum())
                .build());
    }

    @SuppressWarnings("unchecked")
    private void setCellVal(ExcelField excelField, Field field, Cell cell, Object value) {
        cell.setCellStyle(this.customerMetaStyleMap.get(field.getName()).getBodyStyle());
        if (value == null) {
            cell.setCellValue("");
        } else {
            if (ParamUtils.equals("", excelField.pattern())) {
                if (field.getType().isEnum()) {
                    ExcelEnumConvert excelEnumConvert = field.getAnnotation(ExcelEnumConvert.class);
                    Objects.requireNonNull(excelEnumConvert, "Enum convert cannot be null");
                    Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) field.getType();
                    try {
                        EnumConvert<Enum<?>, ?> enumConvert = (EnumConvert<Enum<?>, ?>) excelEnumConvert.convert().newInstance();
                        cell.setCellValue(enumConvert.toExcelAttribute(BeanUtils.getEnum(enumType, value.toString())).toString());
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    String s = value.toString();
                    int len = s.contains(".") ? s.substring(0, s.indexOf(".")).length() : s.length();
                    if (field.getType() != String.class && ParamUtils.isNumber(s) && len < 17) {
                        cell.setCellValue(new BigDecimal(s).doubleValue());
                    } else {
                        cell.setCellValue(s);
                    }
                }
            } else {
                if (this.formatMap == null) {
                    this.formatMap = new HashMap<>(16);
                    SimpleDateFormat format = new SimpleDateFormat(excelField.pattern());
                    this.formatMap.put(field.getName(), format);
                    cell.setCellValue(format.format(value));
                } else {
                    SimpleDateFormat format = formatMap.get(field.getName());
                    if (format == null) {
                        format = new SimpleDateFormat(excelField.pattern());
                        this.formatMap.put(field.getName(), format);
                    }
                    cell.setCellValue(format.format(value));
                }
            }
        }
    }

    private boolean addValid(Field field, Row row, int i, boolean locked, Sheet sheet, MetaObject metaObject) {
        ExplicitValid ev = field.getAnnotation(ExplicitValid.class);
        DateValid dv = field.getAnnotation(DateValid.class);
        NumericValid nv = field.getAnnotation(NumericValid.class);
        if (ev != null) {
            try {
                int firstRow = row.getRowNum() + 1;
                locked = ev.validClass().newInstance().valid(ev, this.workbook, sheet, firstRow, ev.boxLastRow() == 0 ? firstRow : ev.boxLastRow() + firstRow - 1, i, i, locked, field.getName()
                        , metaObject.getExplicitValues());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return locked;
        }
        if (dv != null) {
            int firstRow = row.getRowNum() + 1;
            try {
                dv.validClass().newInstance().valid(dv, sheet, firstRow, dv.boxLastRow() == 0 ? firstRow : dv.boxLastRow() + firstRow - 1, i, i);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return locked;
        }
        if (nv != null) {
            int firstRow = row.getRowNum() + 1;
            try {
                nv.validClass().newInstance().valid(nv, sheet, firstRow, nv.boxLastRow() == 0 ? firstRow : nv.boxLastRow() + firstRow - 1, i, i);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return locked;
        }
        return locked;
    }

    private void sum(Sheet sheet, int colIndex, int rowIndex, String formula, Sum sum) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
            CellStyle cellStyle = this.workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Cell remarkCell = row.createCell(0);
            Font font = this.workbook.createFont();
            font.setBold(true);
            cellStyle.setFont(font);
            remarkCell.setCellStyle(cellStyle);
            remarkCell.setCellValue("合计：");
        }
        Cell sumCell = row.createCell(colIndex);
        sumCell.setCellFormula("SUM(" + formula + ")");
        CellStyle sumStyle = this.workbook.createCellStyle();
        sumStyle.setAlignment(HorizontalAlignment.CENTER);
        sumStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        sumStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(sum.format()));
        sumCell.setCellStyle(sumStyle);
    }
}
