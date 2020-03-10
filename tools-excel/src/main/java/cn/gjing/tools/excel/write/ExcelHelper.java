package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.*;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.listen.DataConvert;
import cn.gjing.tools.excel.listen.EnumConvert;
import cn.gjing.tools.excel.listen.MergeCallback;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.valid.ExcelDateValid;
import cn.gjing.tools.excel.valid.ExcelDropdownBox;
import cn.gjing.tools.excel.valid.ExcelNumericValid;
import com.google.gson.Gson;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gjing
 **/
class ExcelHelper {
    private Workbook workbook;
    private Map<Integer, String> formulaMap;
    private Map<String, MetaStyle> customerMetaStyleMap;
    private Map<String, EnumConvert<Enum<?>, ?>> enumConvertMap;
    private Map<String, DataConvert<?, ?>> dataConvertMap;
    private Map<String, MergeCallback<?>> mergeCallbackMap;
    private Gson gson;

    public ExcelHelper(Workbook workbook) {
        this.workbook = workbook;
        this.gson = new Gson();
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

    public int setHead(List<?> data, List<Field> headFieldList, Sheet sheet, boolean changed, int rowIndex, MetaObject metaObject, Excel excel) {
        boolean locked = false;
        if (changed) {
            rowIndex = rowIndex == 0 ? 0 : rowIndex + 1;
            Row headRow = sheet.createRow(rowIndex);
            headRow.setHeight(excel.headHeight());
            MetaStyle metaStyle;
            for (int i = 0, headFieldSize = headFieldList.size(); i < headFieldSize; i++) {
                Cell headCell = headRow.createCell(i);
                Field field = headFieldList.get(i);
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                if (data == null || data.isEmpty()) {
                    locked = this.addValid(field, headRow, i, locked, sheet, metaObject);
                    if (!"".equals(excelField.format())) {
                        CellStyle defaultColumnStyle = this.workbook.createCellStyle();
                        defaultColumnStyle.setDataFormat(this.workbook.createDataFormat().getFormat(excelField.format()));
                        sheet.setDefaultColumnStyle(i, defaultColumnStyle);
                    }
                }
                metaStyle = this.initStyle(metaObject, field, excelField);
                headCell.setCellStyle(metaStyle.getHeadStyle());
                sheet.setColumnWidth(i, excelField.width());
                headCell.setCellValue(excelField.value());
                this.customerMetaStyleMap.put(field.getName(), metaStyle);
                if (excelField.convert() != DefaultDataConvert.class) {
                    if (this.dataConvertMap == null) {
                        this.dataConvertMap = new HashMap<>(16);
                    }
                    if (dataConvertMap.get(field.getName()) == null) {
                        try {
                            this.dataConvertMap.put(field.getName(), excelField.convert().newInstance());
                        } catch (Exception e) {
                            throw new ExcelInitException("Init specified excel header data convert failure " + field.getName() + ", " + e.getMessage());
                        }
                    }
                }
                if (excelField.autoMerge().open()) {
                    if (this.mergeCallbackMap == null) {
                        this.mergeCallbackMap = new HashMap<>(16);
                    }
                    if (this.mergeCallbackMap.get(field.getName()) == null) {
                        try {
                            this.mergeCallbackMap.put(field.getName(), excelField.autoMerge().callback().newInstance());
                        } catch (Exception e) {
                            throw new ExcelInitException("Init specified excel header merge callback failure " + field.getName() + ", " + e.getMessage());
                        }
                    }
                }
            }
        }
        return rowIndex;
    }

    public void setValue(List<?> data, List<Field> headFieldList, Sheet sheet, int rowIndex) {
        rowIndex++;
        Map<Object, ExcelOldModel> excelModelMap = new HashMap<>(16);
        for (int i = 0, dataSize = data.size(); i < dataSize; i++) {
            Object o = data.get(i);
            Row valueRow = sheet.createRow(rowIndex + i);
            for (int j = 0, headSize = headFieldList.size(); j < headSize; j++) {
                Field field = headFieldList.get(j);
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                Object value = BeanUtils.getFieldValue(o, field);
                Cell valueCell = valueRow.createCell(j);
                try {
                    this.sumOrMerge(sheet, excelModelMap, i, dataSize, valueRow, j, excelField, value, valueCell, field, o);
                    this.setCellVal(excelField, field, valueCell, value, o);
                } catch (Exception e) {
                    throw new ExcelResolverException(e.getMessage());
                }
            }
        }
    }

    private void sumOrMerge(Sheet sheet, Map<Object, ExcelOldModel> excelModelMap, int i, int dataSize, Row valueRow, int j, ExcelField excelField, Object value,
                            Cell valueCell, Field field, Object obj) {
        if (i == 0) {
            if (excelField.sum().open()) {
                if (formulaMap == null) {
                    this.formulaMap = new HashMap<>(16);
                }
                this.formulaMap.put(j, valueCell.getAddress().formatAsString() + ":");
            }
            if (excelField.autoMerge().open()) {
                this.putExcelModel(valueRow, value, excelModelMap, j);
            }
            return;
        }
        if (excelField.autoMerge().open()) {
            MergeCallback<?> mergeCallback = this.mergeCallbackMap.get(field.getName());
            ExcelOldModel excelOldModel = excelModelMap.get(j);
            if (excelOldModel != null) {
                if (mergeCallback.toMerge(this.gson.fromJson(this.gson.toJson(obj), (java.lang.reflect.Type) obj.getClass()), field, j, i)) {
                    if (ParamUtils.equals(value, excelOldModel.getOldValue(), excelField.autoMerge().empty())) {
                        if (i == dataSize - 1) {
                            sheet.addMergedRegion(new CellRangeAddress(excelOldModel.getOldRowIndex(), valueRow.getRowNum(), j, j));
                        }
                    } else {
                        if (excelOldModel.getOldRowIndex() + 1 < valueRow.getRowNum()) {
                            sheet.addMergedRegion(new CellRangeAddress(excelOldModel.getOldRowIndex(), valueRow.getRowNum() - 1, j, j));
                        }
                        if (i != dataSize - 1) {
                            this.putExcelModel(valueRow, value, excelModelMap, j);
                        }
                    }
                    return;
                }
                if (excelOldModel.getOldRowIndex() + 1 < valueRow.getRowNum()) {
                    sheet.addMergedRegion(new CellRangeAddress(excelOldModel.getOldRowIndex(), valueRow.getRowNum() - 1, j, j));
                }
                excelOldModel.setOldValue(value);
                excelOldModel.setOldRowIndex(valueRow.getRowNum());
                excelModelMap.put(j, excelOldModel);
            }
        }
        if (i == dataSize - 1) {
            if (excelField.sum().open()) {
                String formula = formulaMap.get(j) + valueCell.getAddress().formatAsString();
                Row row = sheet.getRow(valueCell.getAddress().getRow() + 1);
                if (row == null) {
                    row = sheet.createRow(valueCell.getAddress().getRow() + 1);
                    row.setHeight(excelField.sum().height());
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

    @SuppressWarnings("unchecked")
    private void setCellVal(ExcelField excelField, Field field, Cell cell, Object value, Object obj) {
        cell.setCellStyle(this.customerMetaStyleMap.get(field.getName()).getBodyStyle());
        if (this.dataConvertMap != null) {
            DataConvert<?, ?> dataConvert = this.dataConvertMap.get(field.getName());
            if (dataConvert != null) {
                dataConvert.toExcelAttribute(cell, this.gson.fromJson(this.gson.toJson(obj), (java.lang.reflect.Type) obj.getClass()), value, field, excelField);
                return;
            }
        }
        if (value == null) {
            return;
        }
        if (field.getType().isEnum()) {
            if (this.enumConvertMap == null) {
                this.enumConvertMap = new HashMap<>(16);
            }
            EnumConvert<Enum<?>, ?> enumConvert = this.enumConvertMap.get(field.getName());
            Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) field.getType();
            if (enumConvert == null) {
                ExcelEnumConvert excelEnumConvert = field.getAnnotation(ExcelEnumConvert.class);
                ParamUtils.requireNonNull(excelEnumConvert, field.getName() + " was not found enum convert");
                try {
                    enumConvert = (EnumConvert<Enum<?>, ?>) excelEnumConvert.convert().newInstance();
                    value = enumConvert.toExcelAttribute(BeanUtils.getEnum(enumType, value.toString()));
                    cell.setCellValue(value == null ? "" : value.toString());
                    this.enumConvertMap.put(field.getName(), enumConvert);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                value = enumConvert.toExcelAttribute(BeanUtils.getEnum(enumType, value.toString()));
                cell.setCellValue(value == null ? "" : value.toString());
            }
            return;
        }
        if (field.getType() == Date.class) {
            cell.setCellValue((Date) value);
            return;
        }
        if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
            return;
        }
        if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
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

    private boolean addValid(Field field, Row row, int i, boolean locked, Sheet sheet, MetaObject metaObject) {
        ExcelDropdownBox ev = field.getAnnotation(ExcelDropdownBox.class);
        ExcelDateValid dv = field.getAnnotation(ExcelDateValid.class);
        ExcelNumericValid nv = field.getAnnotation(ExcelNumericValid.class);
        int firstRow = row.getRowNum() + 1;
        if (ev != null) {
            try {
                locked = ev.validClass().newInstance().valid(ev, this.workbook, sheet, firstRow, ev.rows() == 0 ? firstRow : ev.rows() + firstRow - 1, i, i, locked, field.getName()
                        , metaObject.getExplicitValues());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ExcelResolverException("Add specified excel header dropdown box failure " + field.getName() + ", " + e.getMessage());
            }
        }
        if (dv != null) {
            try {
                dv.validClass().newInstance().valid(dv, sheet, firstRow, dv.rows() == 0 ? firstRow : dv.rows() + firstRow - 1, i, i);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ExcelResolverException("Add specified excel header date validation failure " + field.getName() + ", " + e.getMessage());
            }
        }
        if (nv != null) {
            try {
                nv.validClass().newInstance().valid(nv, sheet, firstRow, nv.rows() == 0 ? firstRow : nv.rows() + firstRow - 1, i, i);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ExcelResolverException("Add specified excel header numeric validation failure " + field.getName() + ", " + e.getMessage());
            }
        }
        return locked;
    }

    private MetaStyle initStyle(MetaObject metaObject, Field field, ExcelField excelField) {
        MetaStyle metaStyle;
        if (excelField.style() == DefaultExcelStyle.class) {
            if (!"".equals(excelField.format())) {
                metaStyle = new MetaStyle();
                metaStyle.setHeadStyle(metaObject.getMetaStyle().getHeadStyle());
                CellStyle formatStyle = this.workbook.createCellStyle();
                formatStyle.cloneStyleFrom(metaObject.getMetaStyle().getBodyStyle());
                formatStyle.setDataFormat(this.workbook.createDataFormat().getFormat(excelField.format()));
                metaStyle.setBodyStyle(formatStyle);
            } else {
                metaStyle = metaObject.getMetaStyle();
            }
        } else {
            metaStyle = this.customerMetaStyleMap.get(field.getName());
            ExcelStyle excelStyle;
            if (metaStyle == null) {
                try {
                    excelStyle = excelField.style().newInstance();
                } catch (Exception e) {
                    throw new ExcelInitException("Init specified excel header style failure " + field.getName() + ", " + e.getMessage());
                }
                metaStyle = new MetaStyle();
                CellStyle formatStyle = excelStyle.setBodyStyle(this.workbook, this.workbook.createCellStyle());
                if (!"".equals(excelField.format())) {
                    formatStyle.setDataFormat(this.workbook.createDataFormat().getFormat(excelField.format()));
                }
                metaStyle.setBodyStyle(formatStyle);
                metaStyle.setHeadStyle(excelStyle.setHeaderStyle(this.workbook, this.workbook.createCellStyle()));
            }
        }
        return metaStyle;
    }

    private void putExcelModel(Row row, Object value, Map<Object, ExcelOldModel> excelModelMap, int key) {
        excelModelMap.put(key, ExcelOldModel.builder()
                .oldValue(value)
                .oldRowIndex(row.getRowNum())
                .build());
    }
}
