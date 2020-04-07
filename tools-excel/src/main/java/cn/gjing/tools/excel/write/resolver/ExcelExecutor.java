package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.BigTitle;
import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.convert.*;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ExcelUtils;
import cn.gjing.tools.excel.util.ListenerUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.callback.AutoMergeCallback;
import cn.gjing.tools.excel.write.listener.BaseCascadingDropdownBoxListener;
import cn.gjing.tools.excel.write.listener.BaseCellWriteListener;
import cn.gjing.tools.excel.write.listener.BaseRowWriteListener;
import cn.gjing.tools.excel.write.listener.WriteListener;
import cn.gjing.tools.excel.write.merge.ExcelOldCellModel;
import cn.gjing.tools.excel.write.merge.ExcelOldRowModel;
import cn.gjing.tools.excel.write.style.BaseExcelStyleListener;
import cn.gjing.tools.excel.write.valid.ExcelDateValid;
import cn.gjing.tools.excel.write.valid.ExcelDropdownBox;
import cn.gjing.tools.excel.write.valid.ExcelNumericValid;
import com.google.gson.Gson;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gjing
 **/
class ExcelExecutor {
    private Workbook workbook;
    private Map<Integer, String> sumFormulaMap;
    private Map<String, EnumConvert<Enum<?>, ?>> enumConvertMap;
    private Map<String, DataConvert<?>> dataConvertMap;
    private Map<String, AutoMergeCallback<?>> mergeCallbackMap;
    private Map<Integer, ExcelOldRowModel> oldRowModelMap;
    private ExcelOldCellModel oldCellModel;
    private List<WriteListener> cellListeners;
    private List<WriteListener> rowListeners;
    private Map<Class<? extends WriteListener>, List<WriteListener>> writeListenerMap;
    private Gson gson;

    public ExcelExecutor(Workbook workbook, Map<Class<? extends WriteListener>, List<WriteListener>> writeListenerMap) {
        this.workbook = workbook;
        this.gson = new Gson();
        this.cellListeners = writeListenerMap.get(BaseCellWriteListener.class);
        this.rowListeners = writeListenerMap.get(BaseRowWriteListener.class);
        this.writeListenerMap = writeListenerMap;
    }

    /**
     * Set Excel big title
     *
     * @param bigTitle Big title
     * @param sheet    Current sheet
     */
    public void setBigTitle(BigTitle bigTitle, Sheet sheet) {
        if (bigTitle != null) {
            int startOffset = sheet.getPhysicalNumberOfRows();
            int endOffset = startOffset + bigTitle.getLines() - 1;
            for (int i = 0; i < bigTitle.getLines(); i++) {
                Row row = sheet.createRow(startOffset + i);
                for (int j = bigTitle.getFirstCol(); j < bigTitle.getLastCols(); j++) {
                    Cell cell = row.createCell(j);
                    this.cellListeners.forEach(e -> {
                        if (e instanceof BaseExcelStyleListener) {
                            ((BaseExcelStyleListener) e).setTitleStyle(cell);
                        }
                    });
                    cell.setCellValue(bigTitle.getContent());
                }
            }
            sheet.addMergedRegion(new CellRangeAddress(startOffset, endOffset, bigTitle.getFirstCol(), bigTitle.getLastCols() - 1));
        }
    }

    /**
     * Set excel header
     *
     * @param headFieldList Excel head field list
     * @param sheet         Current sheet
     * @param needHead      Whether to set header
     * @param boxValues     Excel dropdown box value
     * @param excel         Excel map annotation on entity
     */
    public void setHead(List<Field> headFieldList, List<String[]> headNames, Sheet sheet, boolean needHead, Map<String, String[]> boxValues, Excel excel, boolean needValid) {
        boolean needTriggerRow = false;
        if (needHead) {
            int rowIndex = sheet.getLastRowNum() == 0 ? 0 : sheet.getLastRowNum() + 1;
            Row headRow = null;
            for (int i = 0, headSize = headNames.size(); i < headSize; i++) {
                String[] headNameArr = headNames.get(i);
                Field field = headFieldList.get(i);
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                AutoMergeCallback<?> autoMergeCallback = this.initExtension(field, excelField);
                for (int j = 0, headRowSize = headNameArr.length; j < headRowSize; j++) {
                    String headName = headNameArr[j];
                    headRow = sheet.getRow(rowIndex);
                    if (headRow == null) {
                        headRow = sheet.createRow(rowIndex + j);
                        headRow.setHeight(excel.headHeight());
                        needTriggerRow = true;
                    }
                    Cell headCell = headRow.createCell(i);
                    headCell.setCellValue(headName);
                    if (autoMergeCallback != null) {
                        try {
                            this.autoMergeX(autoMergeCallback, sheet, headRow, field, excelField, j, i, headName, null, headSize, true);
                            this.autoMergeY(autoMergeCallback, sheet, headRow, field, excelField, j, i, headName, null, headRowSize, true);
                        } catch (Exception e) {
                            throw new ExcelResolverException("Auto merge failure, " + e.getMessage());
                        }
                    }
                    if (needValid && j == headRowSize - 1) {
                        try {
                            this.addValid(field, headRow, i, sheet, boxValues);
                        } catch (Exception e) {
                            throw new ExcelResolverException("Add excel validation failure, " + e.getMessage());
                        }
                    }
                    ListenerUtils.completeCell(this.cellListeners, sheet, headRow, headCell, excelField, field, headName, rowIndex, i, true, headName);
                }
                if (needTriggerRow) {
                    ListenerUtils.completeRow(this.rowListeners, sheet, headRow, rowIndex, true);
                }
            }
        }
    }

    /**
     * Set excel body
     *
     * @param data          Export data
     * @param headFieldList Excel head field list
     * @param sheet         Current sheet
     */
    public void setValue(List<?> data, List<Field> headFieldList, Sheet sheet) {
        if (data == null) {
            return;
        }
        int rowIndex = sheet.getLastRowNum() + 1;
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        AutoMergeCallback<?> autoMergeCallback;
        for (int i = 0, dataSize = data.size(); i < dataSize; i++) {
            Object o = data.get(i);
            context.setVariable(o.getClass().getSimpleName(), o);
            Row valueRow = sheet.createRow(rowIndex + i);
            for (int j = 0, headSize = headFieldList.size(); j < headSize; j++) {
                Field field = headFieldList.get(j);
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                if (i == 0) {
                    this.initExtension(field, excelField);
                }
                ExcelDataConvert excelDataConvert = field.getAnnotation(ExcelDataConvert.class);
                Object value = BeanUtils.getFieldValue(o, field);
                Cell valueCell = valueRow.createCell(j);
                context.setVariable(field.getName(), value);
                try {
                    value = this.changeData(field, value, o, parser, excelDataConvert, context);
                    this.sum(sheet, i, dataSize, j, excelField, valueCell);
                    if (excelField.autoMerge().open()) {
                        autoMergeCallback = this.mergeCallbackMap.get(field.getName());
                        this.autoMergeX(autoMergeCallback, sheet, valueRow, field, excelField, i, j, value, o, headSize, false);
                        this.autoMergeY(autoMergeCallback, sheet, valueRow, field, excelField, i, j, value, o, dataSize, false);
                    }
                    this.setCellValue(field, valueCell, value);
                } catch (Exception e) {
                    throw new ExcelResolverException(e.getMessage());
                }
            }
            ListenerUtils.completeRow(this.rowListeners, sheet, valueRow, i, false);
        }
    }

    /**
     * initialize extension function
     *
     * @param field      Current field
     * @param excelField ExcelField annotation on current field
     * @return AutoMergeCallback
     */
    private AutoMergeCallback<?> initExtension(Field field, ExcelField excelField) {
        AutoMergeCallback<?> autoMergeCallback = null;
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
                this.oldRowModelMap = new HashMap<>(16);
            }
            autoMergeCallback = this.mergeCallbackMap.get(field.getName());
            if (autoMergeCallback == null) {
                try {
                    autoMergeCallback = excelField.autoMerge().callback().newInstance();
                    this.mergeCallbackMap.put(field.getName(), autoMergeCallback);
                } catch (Exception e) {
                    throw new ExcelInitException("Init specified excel header merge callback failure " + field.getName() + ", " + e.getMessage());
                }
            }
        }
        if (excelField.sum().open()) {
            if (this.sumFormulaMap == null) {
                this.sumFormulaMap = new HashMap<>(16);
            }
        }
        return autoMergeCallback;
    }

    /**
     * Sum
     *
     * @param sheet      Current sheet
     * @param index      Current data index
     * @param dataSize   Excel data
     * @param colIndex   Current col index
     * @param excelField ExcelField annotation on current field
     * @param valueCell  Current cell
     */
    private void sum(Sheet sheet, int index, int dataSize, int colIndex, ExcelField excelField, Cell valueCell) {
        if (excelField.sum().open()) {
            if (index == 0) {
                this.sumFormulaMap.put(colIndex, valueCell.getAddress().formatAsString() + ":");
                return;
            }
            if (index == dataSize - 1) {
                if (excelField.sum().open()) {
                    String formula = sumFormulaMap.get(colIndex) + valueCell.getAddress().formatAsString();
                    Row row = sheet.getRow(valueCell.getAddress().getRow() + 1);
                    if (row == null) {
                        row = sheet.createRow(valueCell.getAddress().getRow() + 1);
                        row.setHeight(excelField.sum().height());
                        Cell remarkCell = row.createCell(0);
                        CellStyle cellStyle = this.createSumCellStyle(excelField);
                        remarkCell.setCellStyle(cellStyle);
                        remarkCell.setCellValue(excelField.sum().value());
                    }
                    Cell sumCell = row.createCell(colIndex);
                    sumCell.setCellFormula("SUM(" + formula + ")");
                    CellStyle sumStyle = this.createSumCellStyle(excelField);
                    sumStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(excelField.sum().format()));
                    sumCell.setCellStyle(sumStyle);
                }
            }
        }
    }

    /**
     * Vertical merger
     *
     * @param autoMergeCallback AutoMergeCallback
     * @param sheet             Current sheet
     * @param row               Current row
     * @param field             Current field
     * @param excelField        ExcelField annotation on current field
     * @param index             Line index, index type according to isHead
     * @param dataSize          Excel head data size or body data size,
     * @param colIndex          Current col index
     * @param cellValue         Current cell value
     * @param obj               The object corresponding to the current row
     * @param isHead            Whether is head
     */
    private void autoMergeY(AutoMergeCallback<?> autoMergeCallback, Sheet sheet, Row row, Field field, ExcelField excelField, int index, int colIndex,
                            Object cellValue, Object obj, int dataSize, boolean isHead) {
        if (index == 0) {
            autoMergeCallback.mergeY(obj == null ? null : this.gson.fromJson(this.gson.toJson(obj), (Type) obj.getClass()), field, colIndex, index, isHead);
            this.oldRowModelMap.put(colIndex, new ExcelOldRowModel(cellValue, row.getRowNum()));
            return;
        }
        ExcelOldRowModel excelOldRowModel = this.oldRowModelMap.get(colIndex);
        if (autoMergeCallback.mergeY(obj == null ? null : this.gson.fromJson(this.gson.toJson(obj), (Type) obj.getClass()), field, colIndex, index, isHead)) {
            if (ParamUtils.equals(cellValue, excelOldRowModel.getOldRowCellValue(), excelField.autoMerge().empty())) {
                if (index == dataSize - 1) {
                    sheet.addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum(), colIndex, colIndex));
                }
                return;
            }
            if (excelOldRowModel.getOldRowIndex() + 1 < row.getRowNum()) {
                sheet.addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum() - 1, colIndex, colIndex));
            }
            return;
        }
        if (excelOldRowModel.getOldRowIndex() + 1 < row.getRowNum()) {
            sheet.addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum() - 1, colIndex, colIndex));
            excelOldRowModel.setOldRowCellValue(cellValue);
            excelOldRowModel.setOldRowIndex(row.getRowNum());
            this.oldRowModelMap.put(colIndex, excelOldRowModel);
        }
    }

    /**
     * Horizontal merger
     *
     * @param autoMergeCallback autoMergeCallback
     * @param sheet             Current sheet
     * @param row               Current row
     * @param field             Current field
     * @param excelField        ExcelField annotation on current field
     * @param index             Line index, index type according to isHead
     * @param colIndex          Current col index
     * @param cellValue         Current cell value
     * @param obj               The object corresponding to the current row
     * @param colSize           Total col
     * @param isHead            Whether is need
     */
    private void autoMergeX(AutoMergeCallback<?> autoMergeCallback, Sheet sheet, Row row, Field field, ExcelField excelField,
                            int index, int colIndex, Object cellValue, Object obj, int colSize, boolean isHead) {
        if (colIndex == 0) {
            autoMergeCallback.mergeX(obj == null ? null : this.gson.fromJson(this.gson.toJson(obj), (Type) obj.getClass()), field, colIndex, index, isHead);
            this.oldCellModel = new ExcelOldCellModel(cellValue, colIndex);
            return;
        }
        if (autoMergeCallback.mergeX(obj == null ? null : this.gson.fromJson(this.gson.toJson(obj), (Type) obj.getClass()), field, colIndex, index, isHead)) {
            if (ParamUtils.equals(cellValue, this.oldCellModel.getOldCellValue(), excelField.autoMerge().empty())) {
                if (colIndex == colSize - 1) {
                    sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), this.oldCellModel.getOldCellIndex(), colIndex));
                    return;
                }
            }
            if (oldCellModel.getOldCellIndex() + 1 < colIndex) {
                sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), this.oldCellModel.getOldCellIndex(), colIndex - 1));
            }
            return;
        }
        if (this.oldCellModel.getOldCellIndex() + 1 < colIndex) {
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), this.oldCellModel.getOldCellIndex(), colIndex - 1));
            this.oldCellModel.setOldCellIndex(colIndex);
            this.oldCellModel.setOldCellValue(cellValue);
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
     * @param field Current field
     * @param cell  Current cell
     * @param value Attribute values
     */
    @SuppressWarnings("unchecked")
    private void setCellValue(Field field, Cell cell, Object value) {
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
        throw new IllegalArgumentException("Unsupported data type, you can use a data converter");
    }

    /**
     * Call the macro to set Excel data validation
     *
     * @param field     Current field
     * @param row       Current row
     * @param colIndex  Current col index
     * @param sheet     Current sheet
     * @param boxValues Excel dropdown box values
     */
    private void addValid(Field field, Row row, int colIndex, Sheet sheet, Map<String, String[]> boxValues) {
        ExcelDropdownBox ev = field.getAnnotation(ExcelDropdownBox.class);
        ExcelDateValid dv = field.getAnnotation(ExcelDateValid.class);
        ExcelNumericValid nv = field.getAnnotation(ExcelNumericValid.class);
        int firstRow = row.getRowNum() + 1;
        if (ev != null) {
            if ("".equals(ev.link())) {
                ExcelUtils.addDropdownBox(ev, this.workbook, sheet, firstRow, ev.rows() == 0 ? firstRow : ev.rows() + firstRow - 1,
                        colIndex, boxValues.get(field.getName()));
            } else {
                List<WriteListener> dropdownListeners = this.writeListenerMap.get(BaseCascadingDropdownBoxListener.class);
                dropdownListeners.forEach(e -> ((BaseCascadingDropdownBoxListener) e)
                        .addCascadingDropdownBox(ev, this.workbook, sheet, firstRow, ev.rows() == 0 ? firstRow : ev.rows() + firstRow - 1, colIndex, field));
            }
        }
        if (dv != null) {
            ExcelUtils.addDateValid(dv, sheet, firstRow, dv.rows() == 0 ? firstRow : dv.rows() + firstRow - 1, colIndex);
        }
        if (nv != null) {
            ExcelUtils.addNumericValid(nv, sheet, firstRow, nv.rows() == 0 ? firstRow : nv.rows() + firstRow - 1, colIndex);
        }
    }

    /**
     * Set sum cell style
     *
     * @param excelField ExcelField
     * @return CellStyle
     */
    private CellStyle createSumCellStyle(ExcelField excelField) {
        CellStyle sumStyle = this.workbook.createCellStyle();
        sumStyle.setAlignment(excelField.sum().align());
        sumStyle.setVerticalAlignment(excelField.sum().verticalAlign());
        Font font = this.workbook.createFont();
        font.setBold(excelField.sum().bold());
        sumStyle.setFont(font);
        return sumStyle;
    }
}
