package cn.gjing.tools.excel.read.resolver.core;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.convert.DataConvert;
import cn.gjing.tools.excel.convert.DefaultDataConvert;
import cn.gjing.tools.excel.convert.ExcelDataConvert;
import cn.gjing.tools.excel.exception.ExcelAssertException;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.exception.ExcelTemplateException;
import cn.gjing.tools.excel.metadata.ExecType;
import cn.gjing.tools.excel.metadata.RowType;
import cn.gjing.tools.excel.metadata.listener.ExcelListener;
import cn.gjing.tools.excel.read.ExcelReaderContext;
import cn.gjing.tools.excel.read.valid.ExcelAssert;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.JsonUtils;
import cn.gjing.tools.excel.util.ListenerChain;
import cn.gjing.tools.excel.util.ParamUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
class ExcelBindReadExecutor<R> extends ExcelBaseReadExecutor<R> {
    private final Map<Class<? extends DataConvert<?>>, DataConvert<?>> dataConvertMap;

    public ExcelBindReadExecutor(ExcelReaderContext<R> context) {
        super(context);
        this.dataConvertMap = new HashMap<>(16);
        this.dataConvertMap.put(DefaultDataConvert.class, new DefaultDataConvert());
    }

    @Override
    public void read(int headerIndex, String sheetName) {
        if (super.context.isCheckTemplate()) {
            String key = "excelUnqSheet";
            if (super.context.getWorkbook().getSheetIndex(key) == -1) {
                throw new ExcelTemplateException();
            }
            for (Row row : super.context.getWorkbook().getSheet(key)) {
                if (!ParamUtils.equals(ParamUtils.encodeMd5(this.context.getUniqueKey()), row.getCell(0).getStringCellValue(), false)) {
                    throw new ExcelTemplateException();
                }
                break;
            }
            super.context.setCheckTemplate(false);
        }
        super.checkSheet(sheetName);
        this.reader(headerIndex, super.context.getResultReadListener() == null ? null : new ArrayList<>(), super.context.getListenerCache(),
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
        super.saveCurrentRowObj = true;
        boolean continueRead = true;
        ListenerChain.doReadBefore(rowReadListeners);
        for (Row row : super.context.getSheet()) {
            if (!continueRead) {
                break;
            }
            if (row.getRowNum() < headerIndex) {
                continueRead = super.readHeadBefore(rowReadListeners, row);
                continue;
            }
            if (row.getRowNum() == headerIndex) {
                continueRead = super.readHead(rowReadListeners, row);
                continue;
            }
            try {
                r = this.context.getExcelClass().newInstance();
                context.setVariable(super.context.getExcelClass().getSimpleName(), r);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ExcelInitException("Excel entity init failure, " + e.getMessage());
            }
            for (int c = 0, size = super.context.getHeadNames().size(); c < size && super.saveCurrentRowObj; c++) {
                String head = super.context.getHeadNames().get(c);
                if ("ignored".equals(head)) {
                    continue;
                }
                Field field = super.context.getExcelFieldMap().get(head);
                if (field == null) {
                    field = super.context.getExcelFieldMap().get(head + ParamUtils.numberToEn(c));
                }
                if (field == null) {
                    continue;
                }
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                Cell valueCell = row.getCell(c);
                Object value;
                try {
                    if (valueCell != null) {
                        value = super.getValue(r, valueCell, field, head, excelField.trim(), excelField.required(), RowType.BODY, ExecType.BIND);
                        context.setVariable(field.getName(), value);
                        this.assertValue(parser, context, row, c, field, excelField);
                        value = this.convert(r, value, parser, context, field.getAnnotation(ExcelDataConvert.class), this.createDataConvert(field, excelField));
                        value = ListenerChain.doReadCell(rowReadListeners, value, valueCell, row.getRowNum(), c, RowType.BODY);
                        if (value != null) {
                            this.setValue(r, field, value);
                        }
                    } else {
                        if (excelField.required()) {
                            super.saveCurrentRowObj = ListenerChain.doReadEmpty(this.context.getListenerCache(), r, head, null);
                        }
                        context.setVariable(field.getName(), null);
                        this.assertValue(parser, context, row, c, field, excelField);
                        value = this.convert(r, null, parser, context, field.getAnnotation(ExcelDataConvert.class), this.createDataConvert(field, excelField));
                        value = ListenerChain.doReadCell(rowReadListeners, value, null, row.getRowNum(), c, RowType.BODY);
                        if (value != null) {
                            this.setValue(r, field, value);
                        }
                    }
                    context.setVariable(field.getName(), value);
                } catch (Exception e) {
                    if (e instanceof ExcelAssertException) {
                        throw (ExcelAssertException) e;
                    }
                    throw new ExcelResolverException(e.getMessage());
                }
            }
            if (super.saveCurrentRowObj) {
                try {
                    continueRead = ListenerChain.doReadRow(rowReadListeners, r, row, RowType.BODY);
                    if (dataList != null) {
                        dataList.add(r);
                    }
                } catch (Exception e) {
                    throw new ExcelResolverException(e.getMessage());
                }
            }
        }
        ListenerChain.doReadFinish(rowReadListeners);
        if (this.context.getResultReadListener() != null) {
            this.context.getResultReadListener().notify(dataList);
        }
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
        return dataConvert.toEntityAttribute(JsonUtils.toObj(JsonUtils.toJson(entity), (Type) entity.getClass()), value);
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
