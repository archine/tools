package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.convert.*;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ExcelUtils;
import cn.gjing.tools.excel.util.ListenerUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.callback.AutoMergeCallback;
import cn.gjing.tools.excel.write.listener.CascadingDropdownBoxListener;
import cn.gjing.tools.excel.write.listener.CellWriteListener;
import cn.gjing.tools.excel.write.listener.WriteListener;
import cn.gjing.tools.excel.write.merge.ExcelOldCellModel;
import cn.gjing.tools.excel.write.merge.ExcelOldRowModel;
import cn.gjing.tools.excel.write.style.ExcelStyleListener;
import cn.gjing.tools.excel.write.valid.ExcelDateValid;
import cn.gjing.tools.excel.write.valid.ExcelDropdownBox;
import cn.gjing.tools.excel.write.valid.ExcelNumericValid;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
class ExcelWriteExecutor {
    private Workbook workbook;
    private Map<String, DataConvert<?>> dataConvertMap;
    private Map<String, AutoMergeCallback<?>> mergeCallbackMap;
    private Map<Integer, ExcelOldRowModel> oldRowModelMap;
    private Map<Class<? extends WriteListener>, List<WriteListener>> writeListenerMap;
    private Gson gson;

    public ExcelWriteExecutor(Workbook workbook, Map<Class<? extends WriteListener>, List<WriteListener>> writeListenerMap) {
        this.workbook = workbook;
        this.gson = new Gson();
        this.writeListenerMap = writeListenerMap;
    }

    /**
     * Set Excel big title
     *
     * @param bigTitle Big title
     * @param sheet    Current sheet
     */
    public void setBigTitle(BigTitle bigTitle, Sheet sheet) {
        List<WriteListener> cellListeners = writeListenerMap.get(CellWriteListener.class);
        int startOffset = sheet.getPhysicalNumberOfRows();
        int endOffset = startOffset + bigTitle.getLines() - 1;
        for (int i = 0; i < bigTitle.getLines(); i++) {
            Row row = sheet.createRow(startOffset + i);
            for (int j = bigTitle.getFirstCol(); j < bigTitle.getLastCols(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(bigTitle.getContent());
                cellListeners.forEach(e -> {
                    if (e instanceof ExcelStyleListener) {
                        ((ExcelStyleListener) e).setTitleStyle(cell);
                    }
                });
            }
        }
        sheet.addMergedRegion(new CellRangeAddress(startOffset, endOffset, bigTitle.getFirstCol(), bigTitle.getLastCols() - 1));
    }

    /**
     * Set excel head
     *
     * @param headFieldList Excel head field list
     * @param sheet         Current sheet
     * @param needHead      Whether to set header
     * @param boxValues     Excel dropdown box value
     * @param needValid     Whether need add excel valid
     * @param isMulti       Whether is multi head
     * @param headNames     Excel head names
     */
    public void setHead(List<Field> headFieldList, List<String[]> headNames, Sheet sheet, boolean needHead, Map<String, String[]> boxValues,
                        boolean needValid, boolean isMulti) {
        if (headNames.isEmpty()) {
            return;
        }
        if (needHead) {
            int rowIndex = sheet.getLastRowNum() == 0 ? 0 : sheet.getLastRowNum() + 1;
            Row headRow;
            ExcelOldCellModel oldCellModel = null;
            for (int index = 0, headRowSize = headNames.get(0).length; index < headRowSize; index++) {
                headRow = sheet.createRow(rowIndex + index);
                for (int colIndex = 0, headSize = headNames.size(); colIndex < headSize; colIndex++) {
                    Field field = headFieldList.get(colIndex);
                    ExcelField excelField = field.getAnnotation(ExcelField.class);
                    String headName = headNames.get(colIndex)[index];
                    Cell headCell = headRow.createCell(colIndex);
                    headCell.setCellValue(headName);
                    if (isMulti) {
                        if (oldCellModel == null) {
                            oldCellModel = new ExcelOldCellModel();
                        }
                        if (this.oldRowModelMap == null) {
                            this.oldRowModelMap = new HashMap<>(12);
                        }
                        try {
                            ExcelUtils.mergeX(oldCellModel, sheet, headRow, excelField.autoMerge().empty(), colIndex, headName, headSize, true);
                            ExcelUtils.mergeY(this.oldRowModelMap, sheet, headRow, excelField.autoMerge().empty(), index, colIndex, headName, headRowSize, true);
                        } catch (Exception e) {
                            throw new ExcelResolverException("Auto merge failure, " + e.getMessage());
                        }
                    }
                    if (needValid && index == headRowSize - 1) {
                        try {
                            this.addValid(field, headRow, index, sheet, boxValues);
                        } catch (Exception e) {
                            throw new ExcelResolverException("Add excel validation failure, " + e.getMessage());
                        }
                    }
                    ListenerUtils.completeCell(this.writeListenerMap, sheet, headRow, headCell, excelField, field, headName, index, colIndex, true, headName);
                }
                ListenerUtils.completeRow(this.writeListenerMap, sheet, headRow, rowIndex, true);
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
        DataConvert<?> dataConvert;
        for (int index = 0, dataSize = data.size(); index < dataSize; index++) {
            Object o = data.get(index);
            context.setVariable(o.getClass().getSimpleName(), o);
            Row valueRow = sheet.createRow(rowIndex + index);
            for (int colIndex = 0, headSize = headFieldList.size(); colIndex < headSize; colIndex++) {
                Field field = headFieldList.get(colIndex);
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                dataConvert = this.createDataConvert(field, excelField);
                ExcelDataConvert excelDataConvert = field.getAnnotation(ExcelDataConvert.class);
                Object value = BeanUtils.getFieldValue(o, field);
                Cell valueCell = valueRow.createCell(colIndex);
                context.setVariable(field.getName(), value);
                try {
                    value = this.convert(field, value, o, parser, excelDataConvert, context, dataConvert);
                    if (excelField.autoMerge().open()) {
                        autoMergeCallback = this.createMergeCallback(field, excelField);
                        this.autoMergeY(autoMergeCallback, sheet, valueRow, field, excelField, index, colIndex, value, o, dataSize);
                    }
                    this.setCellValue(valueCell, value);
                    ListenerUtils.completeCell(this.writeListenerMap, sheet, valueRow, valueCell, excelField, field, null, index, colIndex, false, value);
                } catch (Exception e) {
                    throw new ExcelResolverException(e.getMessage());
                }
            }
            ListenerUtils.completeRow(this.writeListenerMap, sheet, valueRow, index, false);
        }
    }

    /**
     * initialize data convert
     *
     * @param field      Current field
     * @param excelField ExcelField annotation on current field
     * @return DataConvert
     */
    private DataConvert<?> createDataConvert(Field field, ExcelField excelField) {
        DataConvert<?> dataConvert = null;
        if (excelField.convert() != DefaultDataConvert.class) {
            if (this.dataConvertMap == null) {
                this.dataConvertMap = new HashMap<>(10);
            }
            dataConvert = dataConvertMap.get(field.getName());
            if (dataConvert == null) {
                try {
                    dataConvert = excelField.convert().newInstance();
                    this.dataConvertMap.put(field.getName(), dataConvert);
                } catch (Exception e) {
                    throw new ExcelInitException("Init specified excel header data convert failure " + field.getName() + ", " + e.getMessage());
                }
            }
        }
        return dataConvert;
    }

    /**
     * Init merge callback
     *
     * @param field      Current field
     * @param excelField ExcelField annotation on current field
     * @return AutoMergeCallback
     */
    private AutoMergeCallback<?> createMergeCallback(Field field, ExcelField excelField) {
        if (this.mergeCallbackMap == null) {
            this.mergeCallbackMap = new HashMap<>(12);
            if (this.oldRowModelMap == null) {
                this.oldRowModelMap = new HashMap<>(12);
            }
        }
        AutoMergeCallback<?> autoMergeCallback = this.mergeCallbackMap.get(field.getName());
        if (autoMergeCallback == null) {
            try {
                autoMergeCallback = excelField.autoMerge().callback().newInstance();
                this.mergeCallbackMap.put(field.getName(), autoMergeCallback);
            } catch (Exception e) {
                throw new ExcelInitException("Init specified excel header merge callback failure " + field.getName() + ", " + e.getMessage());
            }
        }
        return autoMergeCallback;
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
     */
    private void autoMergeY(AutoMergeCallback<?> autoMergeCallback, Sheet sheet, Row row, Field field, ExcelField excelField, int index, int colIndex,
                            Object cellValue, Object obj, int dataSize) {
        if (index == 0) {
            autoMergeCallback.mergeY(obj == null ? null : this.gson.fromJson(this.gson.toJson(obj), (Type) obj.getClass()), field, colIndex, index);
            this.oldRowModelMap.put(colIndex, new ExcelOldRowModel(cellValue, row.getRowNum()));
            return;
        }
        ExcelOldRowModel excelOldRowModel = this.oldRowModelMap.get(colIndex);
        if (autoMergeCallback.mergeY(obj == null ? null : this.gson.fromJson(this.gson.toJson(obj), (Type) obj.getClass()), field, colIndex, index)) {
            if (ParamUtils.equals(cellValue, excelOldRowModel.getOldRowCellValue(), excelField.autoMerge().empty())) {
                if (index == dataSize - 1) {
                    sheet.addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum(), colIndex, colIndex));
                }
                return;
            }
            if (excelOldRowModel.getOldRowIndex() + 1 < row.getRowNum()) {
                sheet.addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum() - 1, colIndex, colIndex));
            }
            if (index != dataSize - 1) {
                this.oldRowModelMap.put(colIndex, new ExcelOldRowModel(cellValue, row.getRowNum()));
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
    private Object convert(Field field, Object value, Object obj, ExpressionParser parser, ExcelDataConvert excelDataConvert,
                           EvaluationContext context, DataConvert<?> dataConvert) {
        if (excelDataConvert != null && !"".equals(excelDataConvert.expr1())) {
            return parser.parseExpression(excelDataConvert.expr1()).getValue(context);
        }
        if (dataConvert != null) {
            return dataConvert.toExcelAttribute(this.gson.fromJson(this.gson.toJson(obj), (java.lang.reflect.Type) obj.getClass()), value, field);
        }
        return value;
    }

    /**
     * To the cell assignment
     *
     * @param cell  Current cell
     * @param value Attribute values
     */
    private void setCellValue(Cell cell, Object value) {
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
        if (value instanceof Enum) {
            cell.setCellValue(value.toString());
            return;
        }
        if (value instanceof Date) {
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
        throw new IllegalArgumentException("Unsupported data type, you can use a data convert");
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
                ExcelUtils.addDropdownBox(ev.combobox(), ev.showErrorBox(), ev.rank(), ev.errorTitle(), ev.errorContent(), this.workbook, sheet,
                        firstRow, ev.rows() == 0 ? firstRow : ev.rows() + firstRow - 1, colIndex, boxValues.get(field.getName()));
            } else {
                List<WriteListener> dropdownListeners = this.writeListenerMap.get(CascadingDropdownBoxListener.class);
                dropdownListeners.forEach(e -> ((CascadingDropdownBoxListener) e)
                        .addCascadingDropdownBox(ev, this.workbook, sheet, firstRow, ev.rows() == 0 ? firstRow : ev.rows() + firstRow - 1, colIndex, field));
            }
        }
        if (dv != null) {
            ExcelUtils.addDateValid(dv.operatorType(), dv.expr1(), dv.expr2(), dv.pattern(), sheet, firstRow, dv.rows() == 0 ? firstRow : dv.rows() + firstRow - 1,
                    colIndex, dv.showErrorBox(), dv.rank(), dv.errorTitle(), dv.errorContent(), dv.showTip(), dv.tipTitle(), dv.tipContent());
        }
        if (nv != null) {
            ExcelUtils.addNumericValid(nv.validationType(), nv.operatorType(), nv.expr1(), nv.expr2(), sheet, firstRow, nv.rows() == 0 ? firstRow : nv.rows() + firstRow - 1,
                    colIndex, nv.showErrorBox(), nv.rank(), nv.errorTitle(), nv.errorContent(), nv.showTip(), nv.tipTitle(), nv.tipContent());
        }
    }
}
