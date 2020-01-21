package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.*;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.util.TimeUtils;
import cn.gjing.tools.excel.valid.DateValid;
import cn.gjing.tools.excel.valid.ExplicitValid;
import cn.gjing.tools.excel.valid.NumericValid;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Gjing
 **/
class ExcelHelper {
    private Workbook workbook;
    private MetaObject metaObject;
    private Type type;

    public ExcelHelper(Workbook workbook, Type type) {
        this.workbook = workbook;
        this.type = type;
    }

    public int setBigTitle(List<Field> headFieldList, MetaObject metaObject, Sheet sheet) {
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
        if (changed) {
            offset = offset == 0 ? 0 : offset + 1;
            row = sheet.createRow(offset);
            for (int i = 0; i < headFieldList.size(); i++) {
                cell = row.createCell(i);
                cell.setCellStyle(this.metaObject.getMetaStyle().getHeadStyle());
                Field field = headFieldList.get(i);
                ExcelField excelField = field.getAnnotation(ExcelField.class);
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
        ExcelField excelField;
        Field field;
        Object value = null;
        Map<Object, ExcelModel> excelModelMap = new HashMap<>(16);
        ExcelModel excelModel;
        for (int i = 0, dataSize = data.size(); i < dataSize; i++) {
            Object o = data.get(i);
            row = sheet.createRow(offset + i);
            for (int j = 0, headSize = headFieldList.size(); j < headSize; j++) {
                field = headFieldList.get(j);
                excelField = field.getAnnotation(ExcelField.class);
                field.setAccessible(true);
                try {
                    value = field.get(o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
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
                this.setCellVal(excelField, field, row, value, j);
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
    private void setCellVal(ExcelField excelField, Field field, Row row, Object value, int index) {
        Cell valueCell = row.createCell(index);
        valueCell.setCellStyle(this.metaObject.getMetaStyle().getBodyStyle());
        if (value == null) {
            valueCell.setCellValue("");
        } else {
            if (ParamUtils.equals("", excelField.pattern())) {
                if (field.getType().isEnum()) {
                    ExcelEnumConvert excelEnumConvert = field.getAnnotation(ExcelEnumConvert.class);
                    Objects.requireNonNull(excelEnumConvert, "Enum convert cannot be null");
                    Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) field.getType();
                    try {
                        EnumConvert<Enum<?>, ?> enumConvert = (EnumConvert<Enum<?>, ?>) excelEnumConvert.convert().newInstance();
                        valueCell.setCellValue(enumConvert.toExcelAttribute(BeanUtils.getEnum(enumType, value.toString())).toString());
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    valueCell.setCellValue(value.toString());
                }
            } else {
                valueCell.setCellValue(TimeUtils.dateToString((Date) value, excelField.pattern()));
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
}
