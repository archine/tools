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
import cn.gjing.tools.excel.write.valid.ExcelDateValid;
import cn.gjing.tools.excel.write.valid.ExcelDropdownBox;
import cn.gjing.tools.excel.write.valid.ExcelNumericValid;
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
class WriteExecutor {
    private ExcelWriterContext context;
    private Map<String, DataConvert<?>> dataConvertMap;
    private Map<String, ExcelAutoMergeCallback<?>> mergeCallbackMap;
    private Map<Integer, ExcelOldRowModel> oldRowModelMap;
    private Gson gson;

    public WriteExecutor(ExcelWriterContext context) {
        this.gson = new Gson();
        this.context = context;
    }

    /**
     * Set Excel big title
     *
     * @param bigTitle Big title
     */
    public void setBigTitle(BigTitle bigTitle) {
        List<ExcelWriteListener> cellListeners = this.context.getWriteListenerCache().get(ExcelCellWriteListener.class);
        int startOffset = this.context.getSheet().getPhysicalNumberOfRows();
        int endOffset = startOffset + bigTitle.getLines() - 1;
        for (int i = 0; i < bigTitle.getLines(); i++) {
            Row row = this.context.getSheet().createRow(startOffset + i);
            for (int j = bigTitle.getFirstCol(); j < bigTitle.getLastCols(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(bigTitle.getContent());
                cellListeners.forEach(e -> {
                    if (e instanceof ExcelStyleWriteListener) {
                        ((ExcelStyleWriteListener) e).setTitleStyle(cell);
                    }
                });
            }
        }
        this.context.getSheet().addMergedRegion(new CellRangeAddress(startOffset, endOffset, bigTitle.getFirstCol(), bigTitle.getLastCols()));
    }

    /**
     * Set excel head
     *
     * @param needHead  Whether to set header
     * @param boxValues Excel dropdown box value
     */
    public void setHead(boolean needHead, Map<String, String[]> boxValues) {
        if (this.context.getHeadNames().isEmpty()) {
            return;
        }
        if (needHead) {
            int rowIndex = this.context.getSheet().getLastRowNum() == 0 ? 0 : this.context.getSheet().getLastRowNum() + 1;
            Row headRow;
            ExcelOldCellModel oldCellModel = null;
            for (int index = 0, headRowSize = this.context.getHeadNames().get(0).length; index < headRowSize; index++) {
                headRow = this.context.getSheet().createRow(rowIndex + index);
                for (int colIndex = 0, headSize = this.context.getHeadNames().size(); colIndex < headSize; colIndex++) {
                    Field field = this.context.getExcelFields().get(colIndex);
                    ExcelField excelField = field.getAnnotation(ExcelField.class);
                    String headName = this.context.getHeadNames().get(colIndex)[index];
                    Cell headCell = headRow.createCell(colIndex);
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
                    ListenerChain.doCompleteCell(this.context.getWriteListenerCache(), this.context.getSheet(), headRow, headCell, excelField, field, headName, index, colIndex, true, headName);
                }
                ListenerChain.doCompleteRow(this.context.getWriteListenerCache(), this.context.getSheet(), headRow, rowIndex, true);
            }
        }
    }

    /**
     * Set excel body
     *
     * @param data Export data
     */
    public void setValue(List<?> data) {
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
                    if (excelField.autoMerge().open()) {
                        autoMergeCallback = this.createMergeCallback(field, excelField);
                        this.autoMergeY(autoMergeCallback, valueRow, field, excelField, index, colIndex, value, o, dataSize);
                    }
                    ExcelUtils.setCellValue(valueCell, value);
                    ListenerChain.doCompleteCell(this.context.getWriteListenerCache(), this.context.getSheet(), valueRow, valueCell, excelField, field, null, index, colIndex, false, value);
                } catch (Exception e) {
                    throw new ExcelResolverException(e.getMessage());
                }
            }
            ListenerChain.doCompleteRow(this.context.getWriteListenerCache(), this.context.getSheet(), valueRow, index, false);
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
     * Create merge callback
     *
     * @param field      Current field
     * @param excelField ExcelField annotation on current field
     * @return AutoMergeCallback
     */
    private ExcelAutoMergeCallback<?> createMergeCallback(Field field, ExcelField excelField) {
        if (this.mergeCallbackMap == null) {
            this.mergeCallbackMap = new HashMap<>(12);
            if (this.oldRowModelMap == null) {
                this.oldRowModelMap = new HashMap<>(12);
            }
        }
        ExcelAutoMergeCallback<?> autoMergeCallback = this.mergeCallbackMap.get(field.getName());
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
     * Vertical merge
     *
     * @param autoMergeCallback AutoMergeCallback
     * @param row               Current row
     * @param field             Current field
     * @param excelField        ExcelField annotation on current field
     * @param index             Line index, index type according to isHead
     * @param dataSize          Excel head data size or body data size,
     * @param colIndex          Current col index
     * @param cellValue         Current cell value
     * @param obj               The object corresponding to the current row
     */
    private void autoMergeY(ExcelAutoMergeCallback<?> autoMergeCallback, Row row, Field field, ExcelField excelField, int index, int colIndex,
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
                    this.context.getSheet().addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum(), colIndex, colIndex));
                }
                return;
            }
            if (excelOldRowModel.getOldRowIndex() + 1 < row.getRowNum()) {
                this.context.getSheet().addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum() - 1, colIndex, colIndex));
            }
            if (index != dataSize - 1) {
                this.oldRowModelMap.put(colIndex, new ExcelOldRowModel(cellValue, row.getRowNum()));
            }
            return;
        }
        if (excelOldRowModel.getOldRowIndex() + 1 < row.getRowNum()) {
            this.context.getSheet().addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum() - 1, colIndex, colIndex));
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
        int firstRow = row.getRowNum() + 1;
        if (ev != null) {
            if ("".equals(ev.link())) {
                ExcelUtils.addDropdownBox(ev.combobox(), ev.showErrorBox(), ev.rank(), ev.errorTitle(), ev.errorContent(), this.context.getWorkbook(), this.context.getSheet(),
                        firstRow, ev.rows() == 0 ? firstRow : ev.rows() + firstRow - 1, colIndex, boxValues == null ? null : boxValues.get(field.getName()));
            } else {
                List<ExcelWriteListener> dropdownListeners = this.context.getWriteListenerCache().get(ExcelCascadingDropdownBoxListener.class);
                dropdownListeners.forEach(e -> ((ExcelCascadingDropdownBoxListener) e)
                        .addCascadingDropdownBox(ev, this.context.getWorkbook(), this.context.getSheet(), firstRow, ev.rows() == 0 ? firstRow : ev.rows() + firstRow - 1, colIndex, field));
            }
        }
        if (dv != null) {
            ExcelUtils.addDateValid(dv.operatorType(), dv.expr1(), dv.expr2(), dv.pattern(), this.context.getSheet(), firstRow, dv.rows() == 0 ? firstRow : dv.rows() + firstRow - 1,
                    colIndex, dv.showErrorBox(), dv.rank(), dv.errorTitle(), dv.errorContent(), dv.showTip(), dv.tipTitle(), dv.tipContent());
        }
        if (nv != null) {
            ExcelUtils.addNumericValid(nv.validType(), nv.operatorType(), nv.expr1(), nv.expr2(), this.context.getSheet(), firstRow, nv.rows() == 0 ? firstRow : nv.rows() + firstRow - 1,
                    colIndex, nv.showErrorBox(), nv.rank(), nv.errorTitle(), nv.errorContent(), nv.showTip(), nv.tipTitle(), nv.tipContent());
        }
    }
}
