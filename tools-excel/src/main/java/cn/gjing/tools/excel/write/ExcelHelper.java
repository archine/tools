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
    private MetaObject metaObject;
    private Type type;
    private Map<String, SimpleDateFormat> formatMap;
    private Map<Integer, String> formulaMap;
    private List<Field> fieldList;

    public ExcelHelper(Workbook workbook, Type type) {
        this.workbook = workbook;
        this.type = type;
        this.formatMap = new HashMap<>(16);
        this.formulaMap = new HashMap<>(16);
    }

    public int setBigTitle(List<Field> headFieldList, MetaObject metaObject, Sheet sheet) {
        this.fieldList = headFieldList;
        if (metaObject.getBigTitle() != null) {
            Row row;
            Cell cell;
            int offset = sheet.getLastRowNum() == 0 ? 0 : sheet.getLastRowNum() + 1;
            int titleOffset = offset + metaObject.getBigTitle().getLastRow() - 1;
            sheet.addMergedRegion(new CellRangeAddress(offset, titleOffset, 0, headFieldList.size() - 1));
            for (int i = 0; i < metaObject.getBigTitle().getLastRow(); i++) {
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

    public void setVal(List<?> data, List<Field> headFieldList, Sheet sheet, boolean changed, int offset, MetaObject metaObject) {
        this.metaObject = metaObject;
        Cell cell;
        int validIndex = 0;
        Row row;
        ExcelField excelField;
        Field field;
        if (changed) {
            offset = offset == 0 ? 0 : offset + 1;
            row = sheet.createRow(offset);
            for (int i = 0; i < headFieldList.size(); i++) {
                cell = row.createCell(i);
                field = headFieldList.get(i);
                excelField = field.getAnnotation(ExcelField.class);
                if (excelField.style() == DefaultExcelStyle.class) {
                    cell.setCellStyle(this.metaObject.getMetaStyle().getHeadStyle());
                } else {
                    try {
                        cell.setCellStyle(excelField.style().newInstance().setHeaderStyle(this.workbook.createCellStyle()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cell.setCellValue(excelField.value());
                sheet.setColumnWidth(i, excelField.width());
                if (data == null || data.isEmpty()) {
                    validIndex = this.addValid(field, row, i, validIndex, sheet);
                }
            }
        }
        if (data == null || data.isEmpty()) {
            return;
        }
        offset++;
        Object value;
        Map<Object, ExcelModel> excelModelMap = new HashMap<>(16);
        ExcelModel excelModel;
        for (int i = 0, dataSize = data.size(); i < dataSize; i++) {
            Object o = data.get(i);
            row = sheet.createRow(offset + i);
            for (int j = 0, headSize = headFieldList.size(); j < headSize; j++) {
                field = headFieldList.get(j);
                excelField = field.getAnnotation(ExcelField.class);
                field.setAccessible(true);
                value = BeanUtils.getFieldValue(o, field);
                cell = row.createCell(j);
                if (excelField.autoMerge()) {
                    String key = i + "-" + j;
                    if (i == 0) {
                        this.putExcelModel(row, value, excelModelMap, key);
                    } else {
                        String oldKey = (i - 1) + "-" + j;
                        excelModel = excelModelMap.get(oldKey);
                        if (excelModel != null) {
                            if (ParamUtils.equals(value, excelModel.getOldValue())) {
                                if (i == dataSize - 1) {
                                    sheet.addMergedRegion(new CellRangeAddress(excelModel.getRowIndex(), row.getRowNum(), j, j));
                                } else {
                                    excelModelMap.put(key, excelModel);
                                }
                            } else {
                                if (excelModel.getRowIndex() + 1 < row.getRowNum()) {
                                    sheet.addMergedRegion(new CellRangeAddress(excelModel.getRowIndex(), row.getRowNum() - 1, j, j));
                                }
                                if (i != dataSize - 1) {
                                    this.putExcelModel(row, value, excelModelMap, key);
                                }
                            }
                        }
                    }
                }
                this.setCellVal(excelField, field, cell, value);
                if (i == 0) {
                    if (excelField.sum().open()) {
                        if (formulaMap == null) {
                            this.formulaMap = new HashMap<>();
                        }
                        this.formulaMap.put(j, cell.getAddress().formatAsString() + ":");
                    }
                }
                if (i == dataSize - 1) {
                    String formula = formulaMap.get(j);
                    if (formula != null) {
                        this.formulaMap.put(j, formula + cell.getAddress().formatAsString());
                    }

                }
            }
        }
        if (!this.formulaMap.isEmpty()) {
            this.sum(sheet);
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
        if (excelField.style() == DefaultExcelStyle.class) {
            cell.setCellStyle(this.metaObject.getMetaStyle().getBodyStyle());
        } else {
            try {
                cell.setCellStyle(excelField.style().newInstance().setBodyStyle(this.workbook.createCellStyle()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                SimpleDateFormat format = formatMap.get(field.getName());
                if (format == null) {
                    format = new SimpleDateFormat(excelField.pattern());
                    this.formatMap.put(field.getName(), format);
                }
                cell.setCellValue(format.format(value));
            }
        }
    }

    private int addValid(Field field, Row row, int i, int validIndex, Sheet sheet) {
        ExplicitValid ev = field.getAnnotation(ExplicitValid.class);
        DateValid dv = field.getAnnotation(DateValid.class);
        NumericValid nv = field.getAnnotation(NumericValid.class);
        if (ev != null) {
            String[] values = this.metaObject.getExplicitValues().get(field.getName());
            try {
                ev.validClass().newInstance().valid(ev, this.workbook, sheet, row.getRowNum() + 1, i, i, validIndex, values);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (values == null) {
                return validIndex;
            }
            validIndex++;
            return validIndex;
        }
        if (this.type == Type.XLS) {
            if (dv != null) {
                try {
                    dv.validClass().newInstance().valid(dv, sheet, row.getRowNum() + 1, i, i);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                return validIndex;
            }
        }
        if (nv != null) {
            try {
                nv.validClass().newInstance().valid(nv, sheet, row.getRowNum() + 1, i, i);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return validIndex;
        }
        return validIndex;
    }

    private void sum(Sheet sheet) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        Cell cell;
        Sum sum;
        CellStyle cellStyle;
        for (Map.Entry<Integer, String> f : formulaMap.entrySet()) {
            sum = this.fieldList.get(f.getKey()).getAnnotation(ExcelField.class).sum();
            cellStyle = this.workbook.createCellStyle();
            cell = row.createCell(f.getKey());
            cell.setCellFormula("SUM(" + f.getValue() + ")");
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(sum.format()));
            cell.setCellStyle(cellStyle);
        }
        cell = row.createCell(0);
        cellStyle = this.workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("合计：");
    }
}
