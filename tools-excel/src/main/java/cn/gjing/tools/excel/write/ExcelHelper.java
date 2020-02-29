package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.*;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.valid.ExcelDateValid;
import cn.gjing.tools.excel.valid.ExcelDropdownBox;
import cn.gjing.tools.excel.valid.ExcelNumericValid;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Gjing
 **/
class ExcelHelper {
    private Workbook workbook;
    private Map<String, SimpleDateFormat> formatMap;
    private Map<Integer, String> formulaMap;
    private Map<String, MetaStyle> customerMetaStyleMap;
    private Map<String, EnumConvert<Enum<?>, ?>> enumConvertMap;
    private Map<String, DataConvert<?>> dataConvertMap;

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
            for (int i = 0, headFieldSize = headFieldList.size(); i < headFieldSize; i++) {
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
                if (excelField.convert() != DefaultDataConvert.class) {
                    if (this.dataConvertMap == null) {
                        this.dataConvertMap = new HashMap<>(16);
                    }
                    if (dataConvertMap.get(field.getName()) == null) {
                        try {
                            this.dataConvertMap.put(field.getName(), excelField.convert().newInstance());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
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
                Object value = BeanUtils.getFieldValue(o, field);
                Cell valueCell = valueRow.createCell(j);
                this.sumOrMerge(sheet, excelModelMap, i, dataSize, valueRow, j, excelField, value, valueCell);
                this.setCellVal(excelField, field, valueCell, value);
            }
        }
    }

    private void sumOrMerge(Sheet sheet, Map<Object, ExcelModel> excelModelMap, int i, int dataSize, Row valueRow, int j, ExcelField excelField, Object value, Cell valueCell) {
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
                    Row row = sheet.getRow(valueCell.getAddress().getRow() + 1);
                    if (row == null) {
                        row = sheet.createRow(valueCell.getAddress().getRow() + 1);
                        CellStyle cellStyle = this.workbook.createCellStyle();
                        cellStyle.setAlignment(HorizontalAlignment.CENTER);
                        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        Cell remarkCell = row.createCell(0);
                        Font font = this.workbook.createFont();
                        font.setBold(true);
                        cellStyle.setFont(font);
                        remarkCell.setCellStyle(cellStyle);
                        remarkCell.setCellValue(excelField.sum().value());
                    }
                    Cell sumCell = row.createCell(j);
                    sumCell.setCellFormula("SUM(" + formula + ")");
                    CellStyle sumStyle = this.workbook.createCellStyle();
                    sumStyle.setAlignment(HorizontalAlignment.CENTER);
                    sumStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    sumStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(excelField.sum().format()));
                    sumCell.setCellStyle(sumStyle);
                }
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
        DataConvert<?> dataConvert = this.dataConvertMap.get(field.getName());
        if (dataConvert != null) {
            dataConvert.toExcelAttribute(cell, value, field, excelField);
            return;
        }
        cell.setCellStyle(this.customerMetaStyleMap.get(field.getName()).getBodyStyle());
        if (value == null) {
            cell.setCellValue("");
        } else {
            if (field.getType().isEnum()) {
                if (this.enumConvertMap == null) {
                    this.enumConvertMap = new HashMap<>(16);
                }
                EnumConvert<Enum<?>, ?> enumConvert = this.enumConvertMap.get(field.getName());
                Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) field.getType();
                if (enumConvert == null) {
                    ExcelEnumConvert excelEnumConvert = field.getAnnotation(ExcelEnumConvert.class);
                    Objects.requireNonNull(excelEnumConvert, field.getName() + " was not found enum converter");
                    try {
                        enumConvert = (EnumConvert<Enum<?>, ?>) excelEnumConvert.convert().newInstance();
                        cell.setCellValue(enumConvert.toExcelAttribute(BeanUtils.getEnum(enumType, value.toString())).toString());
                        this.enumConvertMap.put(field.getName(), enumConvert);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    cell.setCellValue(enumConvert.toExcelAttribute(BeanUtils.getEnum(enumType, value.toString())).toString());
                }
                return;
            }
            if (field.getType() == Date.class) {
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
                return;
            }
            String val = value.toString();
            int len = val.contains(".") ? val.substring(0, val.indexOf(".")).length() : val.length();
            if (field.getType() != String.class && ParamUtils.isNumber(val) && len < 17) {
                cell.setCellValue(new BigDecimal(val).doubleValue());
            } else {
                cell.setCellValue(val);
            }
        }
    }

    private boolean addValid(Field field, Row row, int i, boolean locked, Sheet sheet, MetaObject metaObject) {
        ExcelDropdownBox ev = field.getAnnotation(ExcelDropdownBox.class);
        ExcelDateValid dv = field.getAnnotation(ExcelDateValid.class);
        ExcelNumericValid nv = field.getAnnotation(ExcelNumericValid.class);
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
}
