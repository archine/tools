package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.*;
import cn.gjing.tools.excel.convert.*;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.exception.ExcelResolverException;
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
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
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
    private Map<String, DataConvert<?>> dataConvertMap;
    private Map<String, MergeCallback<?>> mergeCallbackMap;
    private Gson gson;

    public ExcelHelper(Workbook workbook) {
        this.workbook = workbook;
        this.gson = new Gson();
        this.customerMetaStyleMap = new HashMap<>(16);
    }

    /**
     * Set Excel big title
     * @param totalCol How many columns are merged
     * @param bigTitle Big title
     * @param metaStyle Meta style
     * @param sheet Current sheet
     */
    public void setBigTitle(int totalCol, BigTitle bigTitle, MetaStyle metaStyle, Sheet sheet) {
        if (bigTitle != null) {
            Row row;
            Cell cell;
            int offset = sheet.getPhysicalNumberOfRows();
            int titleOffset = offset + bigTitle.getLines() - 1;
            sheet.addMergedRegion(new CellRangeAddress(offset, titleOffset, 0, totalCol - 1));
            for (int i = 0; i < bigTitle.getLines(); i++) {
                row = sheet.createRow(offset + i);
                for (int j = 0; j < totalCol; j++) {
                    cell = row.createCell(j);
                    cell.setCellValue(bigTitle.getContent());
                    cell.setCellStyle(metaStyle.getTitleStyle());
                }
            }
        }
    }

    /**
     * Set excel header
     * @param noContent Whether there is data export
     * @param headFieldList Excel head field list
     * @param sheet Current sheet
     * @param needHead Whether to set header
     * @param metaStyle Meta style
     * @param dropdownBoxValues Excel dropdown box value
     * @param excel Excel map annotation on entity
     */
    public void setHead(boolean noContent, List<Field> headFieldList, Sheet sheet, boolean needHead, MetaStyle metaStyle, Map<String, String[]> dropdownBoxValues, Excel excel) {
        boolean locked = false;
        if (needHead) {
            int rowIndex = sheet.getLastRowNum() == 0 ? 0 : sheet.getLastRowNum() + 1;
            Row headRow = sheet.createRow(rowIndex);
            headRow.setHeight(excel.headHeight());
            MetaStyle realMetaStyle;
            for (int i = 0, headFieldSize = headFieldList.size(); i < headFieldSize; i++) {
                Cell headCell = headRow.createCell(i);
                Field field = headFieldList.get(i);
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                realMetaStyle = this.initStyle(metaStyle, field, excelField);
                if (noContent) {
                    locked = this.addValid(field, headRow, i, locked, sheet, dropdownBoxValues);
                    CellStyle bodyStyle = realMetaStyle.getBodyStyle();
                    if (!"".equals(excelField.format())) {
                        short nowFormat = bodyStyle.getDataFormat();
                        bodyStyle.setDataFormat(this.workbook.createDataFormat().getFormat(excelField.format()));
                        bodyStyle.setDataFormat(nowFormat);
                    }
                    sheet.setDefaultColumnStyle(i, bodyStyle);
                } else {
                    this.customerMetaStyleMap.put(field.getName(), realMetaStyle);
                    this.initExtension(field, excelField);
                }
                headCell.setCellStyle(realMetaStyle.getHeadStyle());
                sheet.setColumnWidth(i, excelField.width());
                headCell.setCellValue(excelField.value());
            }
        }
    }

    /**
     * Set excel body
     * @param data Export data
     * @param headFieldList Excel head field list
     * @param sheet Current sheet
     * @param metaStyle Meta style
     * @param initExtension Whether to initialize the extension function
     */
    public void setValue(List<?> data, List<Field> headFieldList, Sheet sheet, MetaStyle metaStyle, boolean initExtension) {
        if (data == null) {
            return;
        }
        int rowIndex = sheet.getLastRowNum() + 1;
        Map<Object, ExcelOldModel> excelModelMap = new HashMap<>(16);
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        MetaStyle realMetaStyle;
        for (int i = 0, dataSize = data.size(); i < dataSize; i++) {
            Object o = data.get(i);
            context.setVariable(o.getClass().getSimpleName(), o);
            Row valueRow = sheet.createRow(rowIndex + i);
            for (int j = 0, headSize = headFieldList.size(); j < headSize; j++) {
                Field field = headFieldList.get(j);
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                realMetaStyle = this.customerMetaStyleMap.get(field.getName());
                if (i == 0 && initExtension) {
                    if (realMetaStyle == null) {
                        realMetaStyle = this.initStyle(metaStyle, field, excelField);
                        this.customerMetaStyleMap.put(field.getName(), realMetaStyle);
                    }
                    this.initExtension(field, excelField);
                }
                ExcelDataConvert excelDataConvert = field.getAnnotation(ExcelDataConvert.class);
                Object value = BeanUtils.getFieldValue(o, field);
                Cell valueCell = valueRow.createCell(j);
                context.setVariable(field.getName(), value);
                try {
                    value = this.changeData(field, value, o, parser, excelDataConvert, context);
                    this.sumOrMerge(sheet, excelModelMap, i, dataSize, valueRow, j, excelField, value, valueCell, field, o);
                    this.setCellValue(field, valueCell, value, realMetaStyle.getBodyStyle());
                } catch (Exception e) {
                    throw new ExcelResolverException(e.getMessage());
                }
            }
        }
    }

    /**
     * initialize extension function
     * @param field Current field
     * @param excelField ExcelField annotation on current field
     */
    private void initExtension(Field field, ExcelField excelField) {
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
        if (excelField.sum().open()) {
            if (this.formulaMap == null) {
                this.formulaMap = new HashMap<>(16);
            }
        }
    }

    /**
     * Summation and merge cells
     *
     * @param sheet         Current sheet
     * @param excelModelMap Excel model map
     * @param i             Current row index
     * @param dataSize      Excel data
     * @param valueRow      Current row
     * @param j             Current col index
     * @param excelField    ExcelField annotation on current field
     * @param value         Current attribute value
     * @param valueCell     Current cell
     * @param field         Current field
     * @param obj           Current object
     */
    private void sumOrMerge(Sheet sheet, Map<Object, ExcelOldModel> excelModelMap, int i, int dataSize, Row valueRow, int j, ExcelField excelField, Object value,
                            Cell valueCell, Field field, Object obj) {
        if (i == 0) {
            if (excelField.sum().open()) {
                this.formulaMap.put(j, valueCell.getAddress().formatAsString() + ":");
            }
            if (excelField.autoMerge().open()) {
                this.mergeCallbackMap.get(field.getName()).init(this.gson.fromJson(this.gson.toJson(obj), (java.lang.reflect.Type) obj.getClass()));
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
                    Cell remarkCell = row.createCell(0);
                    CellStyle cellStyle = this.setSumCellStyle(excelField);
                    remarkCell.setCellStyle(cellStyle);
                    remarkCell.setCellValue(excelField.sum().value());
                }
                Cell sumCell = row.createCell(j);
                sumCell.setCellFormula("SUM(" + formula + ")");
                CellStyle sumStyle = this.setSumCellStyle(excelField);
                sumStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(excelField.sum().format()));
                sumCell.setCellStyle(sumStyle);
            }
        }
    }

    /**
     * Data convert
     *
     * @param field            Current field
     * @param value            Attribute values
     * @param obj              Current object
     * @param parser           El parser
     * @param excelDataConvert excelDataConvert
     * @param context          EL context
     * @return new value
     */
    private Object changeData(Field field, Object value, Object obj, ExpressionParser parser, ExcelDataConvert excelDataConvert, EvaluationContext context) {
        if (excelDataConvert != null && !"".equals(excelDataConvert.expr1())) {
            return parser.parseExpression(excelDataConvert.expr1()).getValue(context);
        }
        if (this.dataConvertMap != null) {
            DataConvert<?> dataConvert = this.dataConvertMap.get(field.getName());
            if (dataConvert != null) {
                return dataConvert.toExcelAttribute(this.gson.fromJson(this.gson.toJson(obj), (java.lang.reflect.Type) obj.getClass()), value, field);
            }
        }
        return value;
    }

    /**
     * To the cell assignment
     *
     * @param field     Current field
     * @param cell      Current cell
     * @param value     Attribute values
     * @param bodyStyle Excel body style
     */
    @SuppressWarnings("unchecked")
    private void setCellValue(Field field, Cell cell, Object value, CellStyle bodyStyle) {
        cell.setCellStyle(bodyStyle);
        if (value == null) {
            return;
        }
        if (value instanceof String) {
            cell.setCellValue(value.toString());
            return;
        }
        if (value instanceof Number) {
            cell.setCellValue(Double.parseDouble(value.toString()));
            return;
        }
        if (value instanceof Date) {
            cell.setCellValue((Date) value);
            return;
        }
        if (value instanceof Enum) {
            if (this.enumConvertMap == null) {
                this.enumConvertMap = new HashMap<>(16);
            }
            EnumConvert<Enum<?>, ?> enumConvert = this.enumConvertMap.get(field.getName());
            Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) field.getType();
            if (enumConvert == null) {
                ExcelEnumConvert excelEnumConvert = field.getAnnotation(ExcelEnumConvert.class);
                if (excelEnumConvert == null) {
                    cell.setCellValue(value.toString());
                    return;
                }
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
        if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
            return;
        }
        if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
            return;
        }
        throw new IllegalArgumentException("Unsupported data type");
    }

    /**
     * Call the macro to set Excel data validation
     *
     * @param field             Current field
     * @param row               Current row
     * @param i                 Current col index
     * @param locked            Level drop-down box interlocking living state
     * @param sheet             Current sheet
     * @param dropdownBoxValues Excel dropdown box values
     * @return lock status
     */
    private boolean addValid(Field field, Row row, int i, boolean locked, Sheet sheet, Map<String, String[]> dropdownBoxValues) {
        ExcelDropdownBox ev = field.getAnnotation(ExcelDropdownBox.class);
        ExcelDateValid dv = field.getAnnotation(ExcelDateValid.class);
        ExcelNumericValid nv = field.getAnnotation(ExcelNumericValid.class);
        int firstRow = row.getRowNum() + 1;
        if (ev != null) {
            try {
                locked = ev.validClass().newInstance().valid(ev, this.workbook, sheet, firstRow, ev.rows() == 0 ? firstRow : ev.rows() + firstRow - 1, i, i, locked, field.getName()
                        , dropdownBoxValues);
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

    /**
     * Init specified excel head style
     *
     * @param metaStyle  Excel meta style
     * @param field      Current field
     * @param excelField ExcelField annotation on current filed
     * @return MetaStyle
     */
    private MetaStyle initStyle(MetaStyle metaStyle, Field field, ExcelField excelField) {
        MetaStyle realMetaStyle;
        if (excelField.style() == DefaultExcelStyle.class) {
            if (!"".equals(excelField.format())) {
                realMetaStyle = new MetaStyle();
                realMetaStyle.setHeadStyle(metaStyle.getHeadStyle());
                CellStyle formatStyle = this.workbook.createCellStyle();
                formatStyle.cloneStyleFrom(metaStyle.getBodyStyle());
                formatStyle.setDataFormat(this.workbook.createDataFormat().getFormat(excelField.format()));
                realMetaStyle.setBodyStyle(formatStyle);
            } else {
                realMetaStyle = metaStyle;
            }
        } else {
            realMetaStyle = this.customerMetaStyleMap.get(field.getName());
            ExcelStyle excelStyle;
            if (realMetaStyle == null) {
                try {
                    excelStyle = excelField.style().newInstance();
                } catch (Exception e) {
                    throw new ExcelInitException("Init specified excel header style failure " + field.getName() + ", " + e.getMessage());
                }
                realMetaStyle = new MetaStyle();
                CellStyle formatStyle = excelStyle.setBodyStyle(this.workbook, this.workbook.createCellStyle());
                if (!"".equals(excelField.format())) {
                    formatStyle.setDataFormat(this.workbook.createDataFormat().getFormat(excelField.format()));
                }
                realMetaStyle.setBodyStyle(formatStyle);
                realMetaStyle.setHeadStyle(excelStyle.setHeaderStyle(this.workbook, this.workbook.createCellStyle()));
            }
        }
        return realMetaStyle;
    }

    /**
     * save last row object
     *
     * @param row           Current row
     * @param value         Current attribute value
     * @param excelModelMap Excel model map
     * @param key           key
     */
    private void putExcelModel(Row row, Object value, Map<Object, ExcelOldModel> excelModelMap, int key) {
        excelModelMap.put(key, ExcelOldModel.builder()
                .oldValue(value)
                .oldRowIndex(row.getRowNum())
                .build());
    }

    /**
     * Set sum cell style
     *
     * @param excelField ExcelField
     * @return CellStyle
     */
    private CellStyle setSumCellStyle(ExcelField excelField) {
        CellStyle sumStyle = this.workbook.createCellStyle();
        sumStyle.setAlignment(excelField.sum().align());
        sumStyle.setVerticalAlignment(excelField.sum().verticalAlign());
        Font font = this.workbook.createFont();
        font.setBold(excelField.sum().bold());
        sumStyle.setFont(font);
        return sumStyle;
    }
}
