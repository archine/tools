package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.convert.DataConvert;
import cn.gjing.tools.excel.convert.DefaultDataConvert;
import cn.gjing.tools.excel.convert.ExcelDataConvert;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.metadata.ExcelFieldProperty;
import cn.gjing.tools.excel.metadata.RowType;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ExcelUtils;
import cn.gjing.tools.excel.util.ListenerChain;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.callback.ExcelAutoMergeCallback;
import cn.gjing.tools.excel.write.merge.ExcelOldRowModel;
import cn.gjing.tools.excel.write.valid.handle.ExcelValidAnnotationHandler;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.annotation.Annotation;
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
     * Set excel head
     *
     * @param needHead  Whether to set header
     * @param boxValues Excel dropdown box value
     */
    public void writeHead(boolean needHead, Map<String, String[]> boxValues) {
        if (!needHead || this.context.getFieldProperties().isEmpty()) {
            this.context.setExistHead(false);
            return;
        }
        Row headRow;
        String[] currentRowHeadArray = new String[this.context.getFieldProperties().size()];
        for (int index = 0; index < this.context.getHeaderSeries(); index++) {
            ListenerChain.doCreateRowBefore(this.context.getListenerCache(), this.context.getSheet(), index, RowType.HEAD);
            headRow = this.context.getSheet().createRow(this.context.getSheet().getPhysicalNumberOfRows());
            headRow.setHeight(this.context.getHeaderHeight());
            for (int colIndex = 0, headSize = this.context.getFieldProperties().size(); colIndex < headSize; colIndex++) {
                ExcelFieldProperty property = this.context.getFieldProperties().get(colIndex);
                String headName = property.getValue()[index];
                currentRowHeadArray[colIndex] = headName;
                Cell headCell = headRow.createCell(headRow.getPhysicalNumberOfCells());
                headName = (String) ListenerChain.doAssignmentBefore(this.context.getListenerCache(), this.context.getSheet(), headRow, headCell,
                        property, index, headCell.getColumnIndex(), RowType.HEAD, headName);
                headCell.setCellValue(headName);
                if (this.context.isNeedValid() && index == this.context.getHeaderSeries() - 1) {
                    try {
                        this.addValid(this.context.getExcelFields().get(colIndex), headRow, headCell.getColumnIndex(), boxValues);
                    } catch (Exception e) {
                        throw new ExcelResolverException("Add excel validation failure, " + e.getMessage());
                    }
                }
                ListenerChain.doCompleteCell(this.context.getListenerCache(), this.context.getSheet(), headRow, headCell, property, index,
                        headCell.getColumnIndex(), RowType.HEAD);
                ListenerChain.doSetHeadStyle(this.context.getListenerCache(), headRow, headCell, property, index, colIndex);
            }
            ListenerChain.doCompleteRow(this.context.getListenerCache(), this.context.getSheet(), headRow, currentRowHeadArray, index, RowType.HEAD);
        }
    }

    /**
     * Set excel body
     *
     * @param data Export data
     */
    public void writeBody(List<?> data) {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        for (int index = 0, dataSize = data.size(); index < dataSize; index++) {
            Object o = data.get(index);
            context.setVariable(o.getClass().getSimpleName(), o);
            ListenerChain.doCreateRowBefore(this.context.getListenerCache(), this.context.getSheet(), index, RowType.BODY);
            Row valueRow = this.context.getSheet().createRow(this.context.getSheet().getPhysicalNumberOfRows());
            valueRow.setHeight(this.context.getBodyHeight());
            for (int colIndex = 0, headSize = this.context.getExcelFields().size(); colIndex < headSize; colIndex++) {
                Field field = this.context.getExcelFields().get(colIndex);
                ExcelFieldProperty property = this.context.getFieldProperties().get(colIndex);
                Object value = BeanUtils.getFieldValue(o, field);
                Cell valueCell = valueRow.createCell(valueRow.getPhysicalNumberOfCells());
                context.setVariable(field.getName(), value);
                try {
                    value = this.convert(value, o, parser, field.getAnnotation(ExcelDataConvert.class), context,
                            this.createDataConvert(colIndex, property));
                    value = ListenerChain.doAssignmentBefore(this.context.getListenerCache(), this.context.getSheet(),
                            valueRow, valueCell, property, index, valueCell.getColumnIndex(), RowType.BODY, value);
                    ExcelUtils.setCellValue(valueCell, value);
                    if (property.isAutoMerge()) {
                        this.autoMergeY(this.createMergeCallback(colIndex, property), valueRow, property.isMergeEmpty(), index,
                                valueCell.getColumnIndex(), value, o, dataSize, field);
                    }
                    ListenerChain.doCompleteCell(this.context.getListenerCache(), this.context.getSheet(), valueRow, valueCell,
                            property, index, valueCell.getColumnIndex(), RowType.BODY);
                    ListenerChain.doSetBodyStyle(this.context.getListenerCache(), valueRow, valueCell, property, index, colIndex);
                } catch (Exception e) {
                    throw new ExcelResolverException(e.getMessage());
                }
            }
            ListenerChain.doCompleteRow(this.context.getListenerCache(), this.context.getSheet(), valueRow, o, index, RowType.BODY);
        }
    }

    /**
     * Set excel header for simple type
     *
     * @param needHead Whether to set header
     */
    public void simpleWriteHead(boolean needHead) {
        if (!needHead || this.context.getFieldProperties().isEmpty()) {
            this.context.setExistHead(false);
            return;
        }
        Row headRow;
        String[] currentRowHeadArray = new String[this.context.getFieldProperties().size()];
        for (int index = 0; index < this.context.getHeaderSeries(); index++) {
            ListenerChain.doCreateRowBefore(this.context.getListenerCache(), this.context.getSheet(), index, RowType.HEAD);
            headRow = this.context.getSheet().createRow(this.context.getSheet().getPhysicalNumberOfRows());
            headRow.setHeight(this.context.getHeaderHeight());
            for (int colIndex = 0, headSize = this.context.getFieldProperties().size(); colIndex < headSize; colIndex++) {
                String headName = this.context.getFieldProperties().get(colIndex).getValue()[index];
                currentRowHeadArray[colIndex] = headName;
                ExcelFieldProperty property = this.context.getFieldProperties().get(colIndex);
                Cell headCell = headRow.createCell(headRow.getPhysicalNumberOfCells());
                headName = (String) ListenerChain.doAssignmentBefore(this.context.getListenerCache(), this.context.getSheet(),
                        headRow, headCell, property, index, headCell.getColumnIndex(), RowType.HEAD, headName);
                headCell.setCellValue(headName);
                ListenerChain.doCompleteCell(this.context.getListenerCache(), this.context.getSheet(), headRow, headCell, property,
                        index, headCell.getColumnIndex(), RowType.HEAD);
                ListenerChain.doSetHeadStyle(this.context.getListenerCache(), headRow, headCell, property, index, colIndex);
            }
            ListenerChain.doCompleteRow(this.context.getListenerCache(), this.context.getSheet(), headRow, currentRowHeadArray,
                    index, RowType.HEAD);
        }
    }

    /**
     * Set excel body for simple type
     *
     * @param data Exported data
     */
    public void simpleWriteBody(List<List<Object>> data) {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        for (int index = 0, dataSize = data.size(); index < dataSize; index++) {
            List<?> o = data.get(index);
            ListenerChain.doCreateRowBefore(this.context.getListenerCache(), this.context.getSheet(), index, RowType.BODY);
            Row valueRow = this.context.getSheet().createRow(this.context.getSheet().getPhysicalNumberOfRows());
            valueRow.setHeight(this.context.getBodyHeight());
            for (int colIndex = 0, headSize = this.context.getFieldProperties().size(); colIndex < headSize; colIndex++) {
                Object value = o.get(colIndex);
                ExcelFieldProperty property = this.context.getFieldProperties().get(colIndex);
                Cell valueCell = valueRow.createCell(valueRow.getPhysicalNumberOfCells());
                try {
                    value = this.convert(value, o, parser, null, context, this.createDataConvert(colIndex, property));
                    value = ListenerChain.doAssignmentBefore(this.context.getListenerCache(), this.context.getSheet(), valueRow, valueCell,
                            property, index, valueCell.getColumnIndex(), RowType.BODY, value);
                    ExcelUtils.setCellValue(valueCell, value);
                    if (property.isAutoMerge()) {
                        this.autoMergeY(this.createMergeCallback(colIndex, property), valueRow, property.isMergeEmpty(), index,
                                valueCell.getColumnIndex(), value, o, dataSize, null);
                    }
                    ListenerChain.doCompleteCell(this.context.getListenerCache(), this.context.getSheet(), valueRow, valueCell, property
                            , index, valueCell.getColumnIndex(), RowType.BODY);
                    ListenerChain.doSetBodyStyle(this.context.getListenerCache(), valueRow, valueCell, property, index, colIndex);
                } catch (Exception e) {
                    throw new ExcelResolverException(e.getMessage());
                }
            }
            ListenerChain.doCompleteRow(this.context.getListenerCache(), this.context.getSheet(), valueRow, o, index, RowType.BODY);
        }
    }

    /**
     * Create data convert
     *
     * @param colIndex Current column index
     * @param property ExcelField property
     * @return DataConvert
     */
    private DataConvert<?> createDataConvert(int colIndex, ExcelFieldProperty property) {
        DataConvert<?> dataConvert = this.dataConvertMap.get(property.getConvert());
        if (dataConvert == null) {
            try {
                dataConvert = property.getConvert().newInstance();
                this.dataConvertMap.put(property.getConvert(), dataConvert);
            } catch (Exception e) {
                throw new ExcelInitException("Init specified excel header data converter failure, column index is" + colIndex + ", " + e.getMessage());
            }
        }
        return dataConvert;
    }

    /**
     * Create merge callback
     *
     * @param colIndex Current column index
     * @param property ExcelField property
     * @return AutoMergeCallback
     */
    private ExcelAutoMergeCallback<?> createMergeCallback(int colIndex, ExcelFieldProperty property) {
        if (this.mergeCallbackMap == null) {
            this.mergeCallbackMap = new HashMap<>(16);
            if (this.oldRowModelMap == null) {
                this.oldRowModelMap = new HashMap<>(16);
            }
        }
        ExcelAutoMergeCallback<?> autoMergeCallback = this.mergeCallbackMap.get(property.getMergeCallback());
        if (autoMergeCallback == null) {
            try {
                autoMergeCallback = property.getMergeCallback().newInstance();
                this.mergeCallbackMap.put(property.getMergeCallback(), autoMergeCallback);
            } catch (Exception e) {
                throw new ExcelInitException("Init specified excel header merge callback failure, column index is" + colIndex + ", " + e.getMessage());
            }
        }
        return autoMergeCallback;
    }

    /**
     * Body Vertical auto merge
     *
     * @param autoMergeCallback AutoMergeCallback
     * @param row               Current row
     * @param mergeEmpty        Whether null merges are allowed
     * @param field             The field corresponding to the current callback has a value only when the simple type is exported
     * @param index             The data index, start at 0
     * @param dataSize          Excel head data size or body data size,
     * @param colIndex          Current col index
     * @param cellValue         Current cell value
     * @param obj               The object corresponding to the current row
     */
    private void autoMergeY(ExcelAutoMergeCallback<?> autoMergeCallback, Row row, boolean mergeEmpty, int index, int colIndex,
                            Object cellValue, Object obj, int dataSize, Field field) {
        if (index == 0) {
            if (autoMergeCallback.mergeY(obj == null ? null : this.gson.fromJson(this.gson.toJson(obj), (Type) obj.getClass()), field, colIndex, index)) {
                this.oldRowModelMap.put(colIndex, new ExcelOldRowModel(cellValue, row.getRowNum()));
            } else {
                this.oldRowModelMap.put(colIndex, new ExcelOldRowModel(autoMergeCallback.getClass(), row.getRowNum()));
            }
            return;
        }
        ExcelOldRowModel excelOldRowModel = this.oldRowModelMap.get(colIndex);
        if (autoMergeCallback.mergeY(obj == null ? null : this.gson.fromJson(this.gson.toJson(obj), (Type) obj.getClass()), field, colIndex, index)) {
            if (ParamUtils.equals(cellValue, excelOldRowModel.getOldRowCellValue(), mergeEmpty)) {
                if (index == dataSize - 1) {
                    this.context.getSheet().addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum(), colIndex, colIndex));
                }
                return;
            }
            if (excelOldRowModel.getOldRowIndex() + 1 < row.getRowNum()) {
                this.context.getSheet().addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum() - 1, colIndex, colIndex));
            }
            excelOldRowModel.setOldRowCellValue(cellValue);
            excelOldRowModel.setOldRowIndex(row.getRowNum());
            return;
        }
        if (index == dataSize - 1) {
            int lastRow = row.getRowNum() - 1;
            if (lastRow > excelOldRowModel.getOldRowIndex()) {
                this.context.getSheet().addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), lastRow, colIndex, colIndex));
            }
            return;
        }
        excelOldRowModel.setOldRowIndex(row.getRowNum());
    }

    /**
     * Data convert
     *
     * @param value            Attribute values
     * @param obj              Current object
     * @param parser           El parser
     * @param excelDataConvert excelDataConvert
     * @param dataConvert      dataConvert
     * @param context          EL context
     * @return new value
     */
    private Object convert(Object value, Object obj, ExpressionParser parser, ExcelDataConvert excelDataConvert,
                           EvaluationContext context, DataConvert<?> dataConvert) {
        if (excelDataConvert != null && !"".equals(excelDataConvert.expr1())) {
            return parser.parseExpression(excelDataConvert.expr1()).getValue(context);
        }
        if (dataConvert != null) {
            return dataConvert.toExcelAttribute(this.gson.fromJson(this.gson.toJson(obj), (Type) obj.getClass()), value);
        }
        return value;
    }

    /**
     * Call the macro to set Excel data validation
     *
     * @param field     Current field
     * @param row       Current row
     * @param colIndex  Current col index
     * @param boxValues Dropdown box value map
     */
    private void addValid(Field field, Row row, int colIndex, Map<String, String[]> boxValues) {
        for (ExcelValidAnnotationHandler validAnnotationHandler : this.context.getValidAnnotationHandlers()) {
            Annotation annotation = field.getAnnotation(validAnnotationHandler.getAnnotationClass());
            if (annotation != null) {
                validAnnotationHandler.handle(annotation, this.context, field, row, colIndex, boxValues);
                break;
            }
        }
    }
}
