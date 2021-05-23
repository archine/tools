package cn.gjing.tools.excel.metadata.resolver;

import cn.gjing.tools.excel.convert.DataConvert;
import cn.gjing.tools.excel.convert.DefaultDataConvert;
import cn.gjing.tools.excel.convert.ExcelDataConvert;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.metadata.ExcelFieldProperty;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.callback.ExcelAutoMergeCallback;
import cn.gjing.tools.excel.write.merge.ExcelOldRowModel;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel writes the core processor
 *
 * @author Gjing
 **/
public abstract class ExcelBaseWriteExecutor {
    protected final ExcelWriterContext context;
    protected final Map<Class<? extends DataConvert<?>>, DataConvert<?>> dataConvertMap;
    protected Map<Class<? extends ExcelAutoMergeCallback<?>>, ExcelAutoMergeCallback<?>> mergeCallbackMap;
    protected Map<Integer, ExcelOldRowModel> oldRowModelMap;
    protected final Gson gson;
    protected final ExpressionParser parser;

    public ExcelBaseWriteExecutor(ExcelWriterContext context) {
        this.context = context;
        this.dataConvertMap = new HashMap<>(16);
        this.dataConvertMap.put(DefaultDataConvert.class, new DefaultDataConvert());
        this.gson = new Gson();
        this.parser = new SpelExpressionParser();
    }

    /**
     * Create data convert
     *
     * @param colIndex Current column index
     * @param property ExcelField property
     * @return DataConvert
     */
    protected DataConvert<?> createDataConvert(int colIndex, ExcelFieldProperty property) {
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
    protected ExcelAutoMergeCallback<?> createMergeCallback(int colIndex, ExcelFieldProperty property) {
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
    protected void autoMergeY(ExcelAutoMergeCallback<?> autoMergeCallback, Row row, boolean mergeEmpty, int index, int colIndex,
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
     * @param excelDataConvert excelDataConvert
     * @param dataConvert      dataConvert
     * @param context          EL context
     * @return new value
     */
    protected Object convert(Object value, Object obj, ExcelDataConvert excelDataConvert, EvaluationContext context, DataConvert<?> dataConvert) {
        if (excelDataConvert != null && !"".equals(excelDataConvert.expr1())) {
            return this.parser.parseExpression(excelDataConvert.expr1()).getValue(context);
        }
        if (dataConvert != null) {
            return dataConvert.toExcelAttribute(this.gson.fromJson(this.gson.toJson(obj), (Type) obj.getClass()), value);
        }
        return value;
    }

    /**
     * Set excel head
     *
     * @param needHead  Whether to set header
     * @param boxValues Excel dropdown box value
     */
    public abstract void writeHead(boolean needHead, Map<String, String[]> boxValues);

    /**
     * Set excel body
     *
     * @param data Export data
     */
    public abstract void writeBody(List<?> data);
}
