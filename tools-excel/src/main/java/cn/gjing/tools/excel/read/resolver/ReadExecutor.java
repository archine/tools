package cn.gjing.tools.excel.read.resolver;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.convert.DataConvert;
import cn.gjing.tools.excel.convert.DefaultDataConvert;
import cn.gjing.tools.excel.convert.ExcelDataConvert;
import cn.gjing.tools.excel.exception.ExcelAssertException;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.exception.ExcelTemplateException;
import cn.gjing.tools.excel.metadata.ExcelReaderResolver;
import cn.gjing.tools.excel.read.ExcelReaderContext;
import cn.gjing.tools.excel.read.listener.ExcelEmptyReadListener;
import cn.gjing.tools.excel.read.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.listener.ExcelRowReadListener;
import cn.gjing.tools.excel.read.valid.ExcelAssert;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ListenerChain;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @author Gjing
 **/
class ReadExecutor<R> implements ExcelReaderResolver<R> {
    private ExcelReaderContext<R> context;
    private Map<Class<? extends DataConvert<?>>, DataConvert<?>> dataConvertMap;
    private Boolean save;

    @Override
    public void init(ExcelReaderContext<R> readerContext) {
        this.context = readerContext;
        this.dataConvertMap = new HashMap<>(16);
        this.dataConvertMap.put(DefaultDataConvert.class, new DefaultDataConvert());
    }

    @Override
    public void read(int headerIndex, String sheetName) {
        Sheet sheet = this.context.getWorkbook().getSheet(sheetName);
        if (sheet == null) {
            throw new ExcelResolverException("The" + sheetName + " is not found in the workbook");
        }
        this.context.setSheet(sheet);
        this.reader(headerIndex, this.context.getResultReadListener() == null ? null : new ArrayList<>());
    }

    /**
     * Start read
     *
     * @param headerIndex Excel header index
     * @param dataList    All data
     */
    private void reader(int headerIndex, List<R> dataList) {
        R r;
        this.save = true;
        boolean stop = false;
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        List<ExcelReadListener> rowReadListeners = this.context.getReadListenersCache().get(ExcelRowReadListener.class);
        Gson gson = new Gson();
        List<Object> otherValues;
        ListenerChain.doReadBefore(rowReadListeners, this.context);
        for (Row row : this.context.getSheet()) {
            if (stop) {
                break;
            }
            if (row.getRowNum() > headerIndex) {
                try {
                    r = this.context.getExcelClass().newInstance();
                    context.setVariable(this.context.getExcelClass().getSimpleName(), r);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new ExcelInitException("Excel entity init failure, " + e.getMessage());
                }
                for (int c = 0, fieldSize = this.context.getExcelFields().size(); c < fieldSize && save; c++) {
                    Field field = this.context.getExcelFields().get(c);
                    ExcelField excelField = field.getAnnotation(ExcelField.class);
                    Cell valueCell = row.getCell(c);
                    Object value;
                    try {
                        if (valueCell != null) {
                            value = this.getValue(r, valueCell, field, excelField, gson);
                            context.setVariable(field.getName(), value);
                            this.assertValue(parser, context, row, c, field, excelField);
                            value = ListenerChain.doReadCell(rowReadListeners, value, field, row.getRowNum(), c, false, true);
                            value = this.convert(field, value, parser, context, this.createDataConvert(field, excelField));
                            if (save && value != null) {
                                this.setValue(r, field, value);
                            }
                        } else {
                            this.allowEmpty(r, field, excelField, row.getRowNum(), c);
                            context.setVariable(field.getName(), null);
                            this.assertValue(parser, context, row, c, field, excelField);
                            value = ListenerChain.doReadCell(rowReadListeners, null, field, row.getRowNum(), c, false, true);
                            value = this.convert(field, value, parser, context, this.createDataConvert(field, excelField));
                            this.setValue(r, field, value);
                        }
                        context.setVariable(field.getName(), value);
                    } catch (Exception e) {
                        if (e instanceof ExcelAssertException) {
                            throw (ExcelAssertException) e;
                        }
                        throw new ExcelResolverException(e.getMessage());
                    }
                }
                if (save) {
                    try {
                        if (dataList != null) {
                            dataList.add(r);
                        }
                        ListenerChain.doReadRow(rowReadListeners, r, null, row.getRowNum(), false, true);
                    } catch (Exception e) {
                        throw new ExcelResolverException(e.getMessage());
                    }
                }
                continue;
            }
            if (this.context.isNeedMetaInfo()) {
                boolean isHead = row.getRowNum() == headerIndex;
                if (isHead) {
                    if (row.getLastCellNum() != this.context.getExcelFields().size()) {
                        if (this.context.isTemplateCheck()) {
                            throw new ExcelTemplateException();
                        }
                    }
                }
                otherValues = new ArrayList<>();
                for (Cell cell : row) {
                    otherValues.add(ListenerChain.doReadCell(rowReadListeners, cell.getStringCellValue(), null, row.getRowNum(), cell.getColumnIndex(), isHead, false));
                }
                stop = ListenerChain.doReadRow(rowReadListeners, null, otherValues, row.getRowNum(), isHead, false);
            }
        }
        ListenerChain.doReadFinish(rowReadListeners, this.context);
        ListenerChain.doResultNotify(this.context.getResultReadListener(), dataList);
    }

    /**
     * Get the value of the cell
     *
     * @param cell       cell
     * @param excelField Excel field of current field
     * @param field      Current field
     * @param gson       Gson
     * @param r          Current row generated row
     * @return value
     */
    private Object getValue(R r, Cell cell, Field field, ExcelField excelField, Gson gson) {
        switch (cell.getCellType()) {
            case _NONE:
            case BLANK:
            case ERROR:
                this.allowEmpty(r, field, excelField, cell.getRowIndex(), cell.getColumnIndex());
                break;
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return gson.fromJson(gson.toJson(cell.getNumericCellValue()), field.getType());
            case FORMULA:
                return gson.fromJson(gson.toJson(cell.getStringCellValue()), field.getType());
            default:
                return cell.getStringCellValue();
        }
        return null;
    }

    /**
     * Data convert
     *
     * @param field      Current field
     * @param value      Attribute values
     * @param parser     El parser
     * @param context    EL context
     * @return new value
     */
    private Object convert(Field field, Object value, ExpressionParser parser, EvaluationContext context, DataConvert<?> dataConvert) {
        ExcelDataConvert excelDataConvert = field.getAnnotation(ExcelDataConvert.class);
        if (excelDataConvert != null && !"".equals(excelDataConvert.expr2())) {
            return parser.parseExpression(excelDataConvert.expr2()).getValue(context);
        }
        return dataConvert.toEntityAttribute(value, field);
    }

    /**
     * Create data convert
     *
     * @param field      Current field
     * @param excelField ExcelField annotation on current field
     * @return DataConvert
     */
    private DataConvert<?> createDataConvert(Field field, ExcelField excelField) {
        DataConvert<?> dataConvert = this.dataConvertMap.get(excelField.convert());
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
     * Set value for the field of the object
     *
     * @param o     object
     * @param field field
     * @param value value
     */
    private void setValue(R o, Field field, Object value) {
        try {
            BeanUtils.setFieldValue(o, field, value);
        } catch (RuntimeException e) {
            if (field.getType() == LocalDate.class) {
                BeanUtils.setFieldValue(o, field, LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault()).toLocalDate());
                return;
            }
            if (field.getType() == LocalDateTime.class) {
                BeanUtils.setFieldValue(o, field, LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault()));
                return;
            }
            throw new IllegalArgumentException("Unsupported data type, you can use a data converter " + field.getName() + " " + value);
        }
    }

    /**
     * Check is not empty strategy
     *
     * @param field      Current field
     * @param excelField ExcelFiled annotation on current filed
     * @param rowIndex   Current row index
     * @param colIndex   Current col index
     * @param r          Current row generated java object
     */
    private void allowEmpty(R r, Field field, ExcelField excelField, int rowIndex, int colIndex) {
        if (excelField.allowEmpty()) {
            return;
        }
        this.save = ListenerChain.doReadEmpty(this.context.getReadListenersCache()
                .get(ExcelEmptyReadListener.class), r, field, excelField, rowIndex, colIndex);
    }

    /**
     * Cell value assert
     *
     * @param parser     El parser
     * @param context    EL context
     * @param row        Current row
     * @param c          Current col index
     * @param field      Current field
     * @param excelField ExcelFiled annotation on current filed
     */
    private void assertValue(ExpressionParser parser, EvaluationContext context, Row row, int c, Field field, ExcelField excelField) {
        ExcelAssert excelAssert = field.getAnnotation(ExcelAssert.class);
        if (excelAssert != null) {
            Boolean test = parser.parseExpression(excelAssert.expr()).getValue(context, Boolean.class);
            if (test != null && !test) {
                throw new ExcelAssertException(excelAssert.message(), excelField, field, row.getRowNum(), c);
            }
        }
    }
}
