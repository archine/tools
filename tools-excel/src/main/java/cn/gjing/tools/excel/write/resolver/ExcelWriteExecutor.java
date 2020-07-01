package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.convert.DataConvert;
import cn.gjing.tools.excel.convert.DefaultDataConvert;
import cn.gjing.tools.excel.convert.ExcelDataConvert;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ExcelUtils;
import cn.gjing.tools.excel.util.ListenerChain;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.callback.ExcelAutoMergeCallback;
import cn.gjing.tools.excel.write.listener.ExcelCascadingDropdownBoxListener;
import cn.gjing.tools.excel.write.listener.ExcelCellWriteListener;
import cn.gjing.tools.excel.write.listener.ExcelWriteListener;
import cn.gjing.tools.excel.write.merge.ExcelOldCellModel;
import cn.gjing.tools.excel.write.merge.ExcelOldRowModel;
import cn.gjing.tools.excel.write.style.ExcelStyleWriteListener;
import cn.gjing.tools.excel.write.valid.*;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gjing
 **/
public final class ExcelWriteExecutor {
    private final ExcelWriterContext context;
    private final Map<Class<? extends DataConvert<?>>, DataConvert<?>> dataConvertMap;
    private Map<Class<? extends ExcelAutoMergeCallback<?>>, ExcelAutoMergeCallback<?>> mergeCallbackMap;
    private Map<Integer, ExcelOldRowModel> oldRowModelMap;
    private final Gson gson;

    public ExcelWriteExecutor(ExcelWriterContext context) {
        this.gson = new Gson();
        this.context = context;
        this.dataConvertMap = new HashMap<>(16);
        this.dataConvertMap.put(DefaultDataConvert.class, new DefaultDataConvert());
    }

    /**
     * Set Excel big title
     *
     * @param bigTitle Big title
     */
    public void writeTitle(BigTitle bigTitle) {
        List<ExcelWriteListener> cellListeners = this.context.getWriteListenerCache().get(ExcelCellWriteListener.class);
        int startOffset = this.context.getSheet().getPhysicalNumberOfRows();
        int endOffset = startOffset + bigTitle.getLines() - 1;
        for (int i = 0; i < bigTitle.getLines(); i++) {
            Row row = this.context.getSheet().createRow(startOffset + i);
            row.setHeight(bigTitle.getRowHeight());
            Cell cell = row.createCell(0);
            cell.setCellValue(bigTitle.getContent());
            cellListeners.forEach(e -> {
                if (e instanceof ExcelStyleWriteListener) {
                    ((ExcelStyleWriteListener) e).setTitleStyle(bigTitle, cell);
                }
            });
        }
        this.context.getSheet().addMergedRegion(new CellRangeAddress(startOffset, endOffset, bigTitle.getFirstCol(), bigTitle.getLastCols()));
    }

    /**
     * Set excel head
     *
     * @param needHead  Whether to set header
     * @param boxValues Excel dropdown box value
     */
    public void writeHead(boolean needHead, Map<String, String[]> boxValues) {
        if (this.context.getHeadNames().isEmpty() || !needHead) {
            return;
        }
        Row headRow;
        ExcelOldCellModel oldCellModel = null;
        int headSize = this.context.getHeadNames().size();
        int rowIndex = this.context.getSheet().getLastRowNum() == 0 ? 0 : this.context.getSheet().getLastRowNum() + 1;
        for (int index = 0, headRowSize = this.context.getHeadNames().get(0).length; index < headRowSize; index++) {
            headRow = this.context.getSheet().createRow(rowIndex + index);
            for (int colIndex = 0; colIndex < headSize; colIndex++) {
                Field field = this.context.getExcelFields().get(colIndex);
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                String headName = this.context.getHeadNames().get(colIndex)[index];
                Cell headCell = headRow.createCell(colIndex);
                headName = (String) ListenerChain.doAssignmentBefore(this.context.getWriteListenerCache(), this.context.getSheet(), headRow, headCell, excelField, field, index, colIndex, true, headName);
                headCell.setCellValue(headName);
                if (this.context.getMultiHead()) {
                    if (oldCellModel == null) {
                        oldCellModel = new ExcelOldCellModel();
                    }
                    if (this.oldRowModelMap == null) {
                        this.oldRowModelMap = new HashMap<>(12);
                    }
                    try {
                        ExcelUtils.mergeX(oldCellModel, this.context.getSheet(), headRow, excelField.autoMerge().empty(), colIndex, headName, headSize, true);
                        ExcelUtils.mergeY(this.oldRowModelMap, this.context.getSheet(), headRow, excelField.autoMerge().empty(), index, colIndex, headName, headRowSize, true);
                    } catch (Exception e) {
                        throw new ExcelResolverException("Auto merge failure, " + e.getMessage());
                    }
                }
                if (this.context.getNeedValid() && index == headRowSize - 1) {
                    try {
                        this.addValid(field, headRow, colIndex, boxValues);
                    } catch (Exception e) {
                        throw new ExcelResolverException("Add excel validation failure, " + e.getMessage());
                    }
                }
                ListenerChain.doCompleteCell(this.context.getWriteListenerCache(), this.context.getSheet(), headRow, headCell, excelField, field, index, colIndex, true);
            }
            ListenerChain.doCompleteRow(this.context.getWriteListenerCache(), this.context.getSheet(), headRow, this.context.getHeadNames(), rowIndex, true);
        }
    }

    /**
     * Set excel body
     *
     * @param data Export data
     */
    public void writeBody(List<?> data) {
        if (data == null) {
            return;
        }
        int rowIndex = this.context.getSheet().getLastRowNum() + 1;
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        ExcelAutoMergeCallback<?> autoMergeCallback;
        DataConvert<?> dataConvert;
        for (int index = 0, dataSize = data.size(); index < dataSize; index++) {
            Object o = data.get(index);
            context.setVariable(o.getClass().getSimpleName(), o);
            Row valueRow = this.context.getSheet().createRow(rowIndex + index);
            for (int colIndex = 0, headSize = this.context.getExcelFields().size(); colIndex < headSize; colIndex++) {
                Field field = this.context.getExcelFields().get(colIndex);
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                dataConvert = this.createDataConvert(field, excelField);
                ExcelDataConvert excelDataConvert = field.getAnnotation(ExcelDataConvert.class);
                Object value = BeanUtils.getFieldValue(o, field);
                Cell valueCell = valueRow.createCell(colIndex);
                context.setVariable(field.getName(), value);
                try {
                    value = this.convert(field, value, o, parser, excelDataConvert, context, dataConvert);
                    value = ListenerChain.doAssignmentBefore(this.context.getWriteListenerCache(), this.context.getSheet(), valueRow, valueCell, excelField, field, index, colIndex, false, value);
                    ExcelUtils.setCellValue(valueCell, value);
                    if (excelField.autoMerge().enable()) {
                        autoMergeCallback = this.createMergeCallback(field, excelField);
                        this.autoMergeY(autoMergeCallback, valueRow, excelField.autoMerge().empty(), index, colIndex, value, o, dataSize, field, null);
                    }
                    ListenerChain.doCompleteCell(this.context.getWriteListenerCache(), this.context.getSheet(), valueRow, valueCell, excelField, field, index, colIndex, false);
                } catch (Exception e) {
                    throw new ExcelResolverException(e.getMessage());
                }
            }
            ListenerChain.doCompleteRow(this.context.getWriteListenerCache(), this.context.getSheet(), valueRow, o, index, false);
        }
    }

    /**
     * Set excel header for simple type
     *
     * @param needHead Whether to set header
     */
    public void simpleWriteHead(boolean needHead) {
        if (this.context.getHeadNames().isEmpty() || !needHead) {
            return;
        }
        Row headRow;
        ExcelOldCellModel oldCellModel = null;
        int headSize = this.context.getHeadNames().size();
        int rowIndex = this.context.getSheet().getLastRowNum() == 0 ? 0 : this.context.getSheet().getLastRowNum() + 1;
        for (int index = 0, headRowSize = this.context.getHeadNames().get(0).length; index < headRowSize; index++) {
            headRow = this.context.getSheet().createRow(rowIndex + index);
            for (int colIndex = 0; colIndex < headSize; colIndex++) {
                String headName = this.context.getHeadNames().get(colIndex)[index];
                Cell headCell = headRow.createCell(colIndex);
                headName = (String) ListenerChain.doAssignmentBefore(this.context.getWriteListenerCache(), this.context.getSheet(), headRow, headCell, null, null, index, colIndex, true, headName);
                headCell.setCellValue(headName);
                if (this.context.getMultiHead()) {
                    if (oldCellModel == null) {
                        oldCellModel = new ExcelOldCellModel();
                    }
                    if (this.oldRowModelMap == null) {
                        this.oldRowModelMap = new HashMap<>(12);
                    }
                    try {
                        ExcelUtils.mergeX(oldCellModel, this.context.getSheet(), headRow, true, colIndex, headName, headSize, true);
                        ExcelUtils.mergeY(this.oldRowModelMap, this.context.getSheet(), headRow, true, index, colIndex, headName, headRowSize, true);
                    } catch (Exception e) {
                        throw new ExcelResolverException("Auto merge failure, " + e.getMessage());
                    }
                }
                ListenerChain.doCompleteCell(this.context.getWriteListenerCache(), this.context.getSheet(), headRow, headCell, null, null, index, colIndex, true);
            }
            ListenerChain.doCompleteRow(this.context.getWriteListenerCache(), this.context.getSheet(), headRow, this.context.getHeadNames(), rowIndex, true);
        }
    }

    /**
     * Set excel body for simple type
     *
     * @param data          Exported data
     * @param mergeEmpty    Whether Whether null merges are allowed
     * @param callbackCache Merge callbacks at export time
     */
    public void simpleWriteBody(List<List<Object>> data, boolean mergeEmpty, Map<String, ExcelAutoMergeCallback<?>> callbackCache) {
        if (data == null) {
            return;
        }
        int rowIndex = this.context.getSheet().getLastRowNum() + 1;
        for (int index = 0, dataSize = data.size(); index < dataSize; index++) {
            List<?> o = data.get(index);
            Row valueRow = this.context.getSheet().createRow(rowIndex + index);
            for (int colIndex = 0, headSize = o.size(); colIndex < headSize; colIndex++) {
                Object value = o.get(colIndex);
                Cell valueCell = valueRow.createCell(colIndex);
                try {
                    value = ListenerChain.doAssignmentBefore(this.context.getWriteListenerCache(), this.context.getSheet(), valueRow, valueCell, null, null, index, colIndex, false, value);
                    ExcelUtils.setCellValue(valueCell, value);
                    String[] headArr = this.context.getHeadNames().get(colIndex);
                    String key = headArr[headArr.length - 1];
                    ExcelAutoMergeCallback<?> mergeCallback = callbackCache.get(key);
                    if (mergeCallback != null) {
                        if (this.oldRowModelMap == null) {
                            this.oldRowModelMap = new HashMap<>(16);
                        }
                        this.autoMergeY(mergeCallback, valueRow, mergeEmpty, index, colIndex, value, o, dataSize, null, key);
                    }
                    ListenerChain.doCompleteCell(this.context.getWriteListenerCache(), this.context.getSheet(), valueRow, valueCell, null, null, index, colIndex, false);
                } catch (Exception e) {
                    throw new ExcelResolverException(e.getMessage());
                }
            }
            ListenerChain.doCompleteRow(this.context.getWriteListenerCache(), this.context.getSheet(), valueRow, o, index, false);
        }
    }

    /**
     * Create data convert
     *
     * @param field      Current field
     * @param excelField ExcelField annotation on current field
     * @return DataConvert
     */
    private DataConvert<?> createDataConvert(Field field, ExcelField excelField) {
        DataConvert<?> dataConvert = dataConvertMap.get(excelField.convert());
        if (dataConvert == null) {
            try {
                dataConvert = excelField.convert().newInstance();
                this.dataConvertMap.put(excelField.convert(), dataConvert);
            } catch (Exception e) {
                throw new ExcelInitException("Init specified excel header data converter failure " + field.getName() + ", " + e.getMessage());
            }
        }
        return dataConvert;
    }

    /**
     * Create merge callback
     *
     * @param field      Current field
     * @param excelField ExcelField annotation on current field
     * @return AutoMergeCallback
     */
    private ExcelAutoMergeCallback<?> createMergeCallback(Field field, ExcelField excelField) {
        if (this.mergeCallbackMap == null) {
            this.mergeCallbackMap = new HashMap<>(16);
            if (this.oldRowModelMap == null) {
                this.oldRowModelMap = new HashMap<>(16);
            }
        }
        ExcelAutoMergeCallback<?> autoMergeCallback = this.mergeCallbackMap.get(excelField.autoMerge().callback());
        if (autoMergeCallback == null) {
            try {
                autoMergeCallback = excelField.autoMerge().callback().newInstance();
                this.mergeCallbackMap.put(excelField.autoMerge().callback(), autoMergeCallback);
            } catch (Exception e) {
                throw new ExcelInitException("Init specified excel header merge callback failure " + field.getName() + ", " + e.getMessage());
            }
        }
        return autoMergeCallback;
    }

    /**
     * Vertical merge
     *
     * @param autoMergeCallback AutoMergeCallback
     * @param row               Current row
     * @param mergeEmpty        Whether null merges are allowed
     * @param field             The field corresponding to the current callback has a value only when the simple type is exported
     * @param key               The key corresponding to the current callback has a value only when the simple type is exported
     * @param index             Line index, index type according to isHead
     * @param dataSize          Excel head data size or body data size,
     * @param colIndex          Current col index
     * @param cellValue         Current cell value
     * @param obj               The object corresponding to the current row
     */
    private void autoMergeY(ExcelAutoMergeCallback<?> autoMergeCallback, Row row, boolean mergeEmpty, int index, int colIndex,
                            Object cellValue, Object obj, int dataSize, Field field, String key) {
        if (index == 0) {
            autoMergeCallback.mergeY(obj == null ? null : this.gson.fromJson(this.gson.toJson(obj), (Type) obj.getClass()), field, key, colIndex, index);
            this.oldRowModelMap.put(colIndex, new ExcelOldRowModel(cellValue, row.getRowNum()));
            return;
        }
        ExcelOldRowModel excelOldRowModel = this.oldRowModelMap.get(colIndex);
        if (autoMergeCallback.mergeY(obj == null ? null : this.gson.fromJson(this.gson.toJson(obj), (Type) obj.getClass()), field, key, colIndex, index)) {
            if (ParamUtils.equals(cellValue, excelOldRowModel.getOldRowCellValue(), mergeEmpty)) {
                if (index == dataSize - 1) {
                    this.context.getSheet().addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum(), colIndex, colIndex));
                }
                return;
            }
            if (excelOldRowModel.getOldRowIndex() + 1 < row.getRowNum()) {
                this.context.getSheet().addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum() - 1, colIndex, colIndex));
            }
            if (index != dataSize - 1) {
                excelOldRowModel.setOldRowCellValue(cellValue);
                excelOldRowModel.setOldRowIndex(row.getRowNum());
                this.oldRowModelMap.put(colIndex, excelOldRowModel);
            }
            return;
        }
        if (excelOldRowModel.getOldRowIndex() + 1 < row.getRowNum()) {
            this.context.getSheet().addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum() - 1, colIndex, colIndex));
        }
        excelOldRowModel.setOldRowCellValue(cellValue);
        excelOldRowModel.setOldRowIndex(row.getRowNum());
        this.oldRowModelMap.put(colIndex, excelOldRowModel);
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
            return dataConvert.toExcelAttribute(this.gson.fromJson(this.gson.toJson(obj), (Type) obj.getClass()), value, field);
        }
        return value;
    }

    /**
     * Call the macro to set Excel data validation
     *
     * @param field    Current field
     * @param row      Current row
     * @param colIndex Current col index
     */
    private void addValid(Field field, Row row, int colIndex, Map<String, String[]> boxValues) {
        ExcelDropdownBox ev = field.getAnnotation(ExcelDropdownBox.class);
        ExcelDateValid dv = field.getAnnotation(ExcelDateValid.class);
        ExcelNumericValid nv = field.getAnnotation(ExcelNumericValid.class);
        ExcelCustomValid ecv = field.getAnnotation(ExcelCustomValid.class);
        ExcelRepeatValid epv = field.getAnnotation(ExcelRepeatValid.class);
        int firstRow = row.getRowNum() + 1;
        if (epv != null) {
            ExcelUtils.addRepeatValid(this.context.getSheet(), firstRow, epv.rows() == 0 ? firstRow : epv.rows() + firstRow - 1, colIndex, epv.showErrorBox(), epv.rank(),
                    epv.errorTitle(), epv.errorContent());
            return;
        }
        if (ev != null) {
            if ("".equals(ev.link())) {
                ExcelUtils.addDropdownBox(ev.combobox(), ev.showErrorBox(), ev.rank(), ev.errorTitle(), ev.errorContent(), this.context.getWorkbook(), this.context.getSheet(),
                        firstRow, ev.rows() == 0 ? firstRow : ev.rows() + firstRow - 1, colIndex, boxValues == null ? null : boxValues.get(field.getName()));
            } else {
                List<ExcelWriteListener> dropdownListeners = this.context.getWriteListenerCache().get(ExcelCascadingDropdownBoxListener.class);
                if (dropdownListeners == null) {
                    return;
                }
                dropdownListeners.forEach(e -> ((ExcelCascadingDropdownBoxListener) e)
                        .addCascadingDropdownBox(ev, this.context.getWorkbook(), this.context.getSheet(), firstRow, ev.rows() == 0 ? firstRow : ev.rows() + firstRow - 1, colIndex, field));
            }
            return;
        }
        if (dv != null) {
            ExcelUtils.addDateValid(dv.operatorType(), dv.expr1(), dv.expr2(), dv.pattern(), this.context.getSheet(), firstRow, dv.rows() == 0 ? firstRow : dv.rows() + firstRow - 1,
                    colIndex, dv.showErrorBox(), dv.rank(), dv.errorTitle(), dv.errorContent(), dv.showTip(), dv.tipTitle(), dv.tipContent());
            return;
        }
        if (nv != null) {
            ExcelUtils.addNumericValid(nv.validType(), nv.operatorType(), nv.expr1(), nv.expr2(), this.context.getSheet(), firstRow, nv.rows() == 0 ? firstRow : nv.rows() + firstRow - 1,
                    colIndex, nv.showErrorBox(), nv.rank(), nv.errorTitle(), nv.errorContent(), nv.showTip(), nv.tipTitle(), nv.tipContent());
            return;
        }
        if (ecv != null) {
            ExcelUtils.addCustomValid(ecv.formula(), this.context.getSheet(), firstRow, ecv.rows() == 0 ? firstRow : ecv.rows() + firstRow - 1,
                    colIndex, ecv.showErrorBox(), ecv.rank(), ecv.errorTitle(), ecv.errorContent());
        }
    }
}
