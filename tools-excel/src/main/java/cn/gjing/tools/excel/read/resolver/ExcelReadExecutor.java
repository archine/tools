package cn.gjing.tools.excel.read.resolver;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.convert.DataConvert;
import cn.gjing.tools.excel.convert.DefaultDataConvert;
import cn.gjing.tools.excel.convert.ExcelDataConvert;
import cn.gjing.tools.excel.exception.ExcelAssertException;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.exception.ExcelTemplateException;
import cn.gjing.tools.excel.metadata.RowType;
import cn.gjing.tools.excel.metadata.listener.ExcelListener;
import cn.gjing.tools.excel.metadata.resolver.ExcelReaderResolver;
import cn.gjing.tools.excel.read.ExcelReaderContext;
import cn.gjing.tools.excel.read.valid.ExcelAssert;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ListenerChain;
import cn.gjing.tools.excel.util.ParamUtils;
import com.google.gson.Gson;
import com.monitorjbl.xlsx.impl.StreamingWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @author Gjing
 **/
class ExcelReadExecutor<R> implements ExcelReaderResolver<R> {
    private ExcelReaderContext<R> context;
    private Map<Class<? extends DataConvert<?>>, DataConvert<?>> dataConvertMap;
    private Boolean save;
    private Gson gson;

    @Override
    public void init(ExcelReaderContext<R> readerContext) {
        this.context = readerContext;
        this.dataConvertMap = new HashMap<>(16);
        this.dataConvertMap.put(DefaultDataConvert.class, new DefaultDataConvert());
        this.gson = new Gson();
    }

    @Override
    public void read(int headerIndex, String sheetName) {
        if (this.context.isCheckTemplate()) {
            String key = "unq-" + sheetName;
            if (this.context.getWorkbook().getSheetIndex(key) == -1) {
                throw new ExcelTemplateException();
            }
            for (Row row : this.context.getWorkbook().getSheet(key)) {
                if (!ParamUtils.equals(ParamUtils.encodeMd5(this.context.getUniqueKey()), row.getCell(0).getStringCellValue(), false)) {
                    throw new ExcelTemplateException();
                }
                break;
            }
        }
        if (this.context.getWorkbook() instanceof StreamingWorkbook) {
            try {
                this.context.setSheet(this.context.getWorkbook().getSheet(sheetName));
            } catch (Exception e) {
                throw new ExcelResolverException("The" + sheetName + " is not found in the workbook");
            }
        } else {
            Sheet sheet = this.context.getWorkbook().getSheet(sheetName);
            if (sheet == null) {
                throw new ExcelResolverException("The" + sheetName + " is not found in the workbook");
            }
            this.context.setSheet(sheet);
        }
        this.reader(headerIndex, this.context.getResultReadListener() == null ? null : new ArrayList<>(), this.context.getListenerCache(),
                new SpelExpressionParser(), new StandardEvaluationContext());
    }

    /**
     * Start read
     *
     * @param headerIndex Excel header index
     * @param dataList    All data
     */
    private void reader(int headerIndex, List<R> dataList, List<ExcelListener> rowReadListeners, ExpressionParser parser, EvaluationContext context) {
        R r;
        this.save = true;
        boolean stop = false;
        List<Object> otherValues;
        ListenerChain.doReadBefore(rowReadListeners);
        for (Row row : this.context.getSheet()) {
            if (stop) {
                break;
            }
            if (row.getRowNum() < headerIndex) {
                if (this.context.isHeadBefore()) {
                    otherValues = new ArrayList<>();
                    for (Cell cell : row) {
                        Object value = this.getValue(null, cell, null, null, RowType.OTHER);
                        otherValues.add(ListenerChain.doReadCell(rowReadListeners, value, null, row.getRowNum(), cell.getColumnIndex(), RowType.OTHER));
                    }
                    stop = ListenerChain.doReadRow(rowReadListeners, null, otherValues, row.getRowNum(), RowType.OTHER);
                }
                continue;
            }
            if (row.getRowNum() == headerIndex) {
                for (Cell cell : row) {
                    String value = cell.getStringCellValue();
                    if (ParamUtils.contains(this.context.getIgnores(), value)) {
                        value = "ignored";
                    }
                    this.context.getHeadNames().add(String.valueOf(ListenerChain.doReadCell(rowReadListeners, value, null, row.getRowNum(), cell.getColumnIndex(), RowType.HEAD)));
                }
                stop = ListenerChain.doReadRow(rowReadListeners, null, this.context.getHeadNames(), row.getRowNum(), RowType.HEAD);
                continue;
            }
            if (row.getRowNum() > headerIndex) {
                try {
                    r = this.context.getExcelClass().newInstance();
                    context.setVariable(this.context.getExcelClass().getSimpleName(), r);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new ExcelInitException("Excel entity init failure, " + e.getMessage());
                }
                for (int c = 0, size = this.context.getHeadNames().size(); c < size && save; c++) {
                    String head = this.context.getHeadNames().get(c);
                    if ("ignored".equals(head)) {
                        continue;
                    }
                    Field field = this.context.getExcelFieldMap().get(head);
                    if (field == null) {
                        field = this.context.getExcelFieldMap().get(head + ParamUtils.numberToEn(c));
                    }
                    if (field == null) {
                        continue;
                    }
                    ExcelField excelField = field.getAnnotation(ExcelField.class);
                    Cell valueCell = row.getCell(c);
                    Object value;
                    try {
                        if (valueCell != null) {
                            value = this.getValue(r, valueCell, field, excelField, RowType.BODY);
                            context.setVariable(field.getName(), value);
                            this.assertValue(parser, context, row, c, field, excelField);
                            value = ListenerChain.doReadCell(rowReadListeners, value, field, row.getRowNum(), c, RowType.BODY);
                            value = this.convert(r, value, parser, context, field.getAnnotation(ExcelDataConvert.class), this.createDataConvert(field, excelField));
                            if (save && value != null) {
                                this.setValue(r, field, value);
                            }
                        } else {
                            this.allowEmpty(r, field, excelField, row.getRowNum(), c);
                            context.setVariable(field.getName(), null);
                            this.assertValue(parser, context, row, c, field, excelField);
                            value = ListenerChain.doReadCell(rowReadListeners, null, field, row.getRowNum(), c, RowType.BODY);
                            value = this.convert(r, value, parser, context, field.getAnnotation(ExcelDataConvert.class), this.createDataConvert(field, excelField));
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
                        ListenerChain.doReadRow(rowReadListeners, r, null, row.getRowNum(), RowType.BODY);
                    } catch (Exception e) {
                        throw new ExcelResolverException(e.getMessage());
                    }
                }
            }
        }
        ListenerChain.doReadFinish(rowReadListeners);
        if (this.context.getResultReadListener() != null) {
            this.context.getResultReadListener().notify(dataList);
        }
    }

    /**
     * Get the value of the cell
     *
     * @param cell       cell
     * @param excelField Excel field of current field
     * @param field      Current field
     * @param r          Current row generated row
     * @param rowType    rowType Current row type
     * @return value
     */
    private Object getValue(R r, Cell cell, Field field, ExcelField excelField, RowType rowType) {
        switch (cell.getCellType()) {
            case _NONE:
            case BLANK:
            case ERROR:
                if (rowType == RowType.BODY) {
                    this.allowEmpty(r, field, excelField, cell.getRowIndex(), cell.getColumnIndex());
                }
                break;
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return rowType == RowType.BODY ? gson.fromJson(gson.toJson(cell.getNumericCellValue()), field.getType()) : cell.getNumericCellValue();
            case FORMULA:
                return rowType == RowType.BODY ? gson.fromJson(gson.toJson(cell.getStringCellValue()), field.getType()) : cell.getStringCellValue();
            default:
                return excelField.trim() ? cell.getStringCellValue().trim() : cell.getStringCellValue();
        }
        return null;
    }

    /**
     * Data convert
     *
     * @param entity           Current entity
     * @param value            Attribute values
     * @param parser           El parser
     * @param dataConvert      dataConvert
     * @param excelDataConvert excelDataConvert
     * @param context          EL context
     * @return new value
     */
    private Object convert(R entity, Object value, ExpressionParser parser, EvaluationContext context, ExcelDataConvert excelDataConvert, DataConvert<?> dataConvert) {
        if (excelDataConvert != null && !"".equals(excelDataConvert.expr2())) {
            return parser.parseExpression(excelDataConvert.expr2()).getValue(context);
        }
        return dataConvert.toEntityAttribute(this.gson.fromJson(this.gson.toJson(entity), (Type) entity.getClass()), value);
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
            throw new IllegalArgumentException("Unsupported data type, the current cell value type is " + value.getClass().getTypeName()
                    + ", but " + field.getName() + " is " + field.getType().getTypeName());
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
        if (excelField.required()) {
            this.save = ListenerChain.doReadEmpty(this.context.getListenerCache(), r, field, rowIndex, colIndex);
        }
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
