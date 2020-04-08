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
import cn.gjing.tools.excel.read.valid.ExcelAssert;
import cn.gjing.tools.excel.read.listener.EmptyReadListener;
import cn.gjing.tools.excel.read.listener.ReadListener;
import cn.gjing.tools.excel.read.listener.ResultReadListener;
import cn.gjing.tools.excel.read.listener.RowReadListener;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ListenerChain;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.*;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Gjing
 **/
class ExcelReadExecutor<R> implements ExcelReaderResolver<R> {
    private Workbook workbook;
    private List<String> headNameList;
    private Map<String, Field> excelFieldMap;
    private Map<String, DataConvert<?>> dataConvertMap;
    private Map<Class<? extends ReadListener>, List<ReadListener>> readListenersMap;
    private Boolean isSave;

    public ExcelReadExecutor(Workbook workbook, Map<Class<? extends ReadListener>, List<ReadListener>> readListenersMap) {
        this.excelFieldMap = new HashMap<>(16);
        this.workbook = workbook;
        this.readListenersMap = readListenersMap;
        this.headNameList = new ArrayList<>();
    }

    @Override
    public void read(Class<R> excelClass, int startIndex, String sheetName, List<Field> excelFieldList, boolean collect) {
        if (excelFieldMap.isEmpty()) {
            this.excelFieldMap = excelFieldList.stream()
                    .peek(e -> {
                        ExcelField excelField = e.getAnnotation(ExcelField.class);
                        if (excelField.convert() != DefaultDataConvert.class) {
                            if (this.dataConvertMap == null) {
                                this.dataConvertMap = new HashMap<>(16);
                            }
                            try {
                                this.dataConvertMap.put(e.getName(), excelField.convert().newInstance());
                            } catch (Exception ex) {
                                throw new ExcelInitException("Init specified excel header data convert error " + e.getName() + ", " + ex.getMessage());
                            }
                        }
                    }).collect(Collectors.toMap(field -> {
                        ExcelField excelField = field.getAnnotation(ExcelField.class);
                        int headSize = excelField.value().length;
                        return excelField.value()[headSize == 0 ? 0 : headSize - 1];
                    }, field -> field));
        }
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new ExcelResolverException("The" + sheetName + " is not found in the workbook");
        }
        this.reader(sheet, excelClass, startIndex, collect ? new ArrayList<>() : null, collect);
    }

    /**
     * Start read
     *
     * @param sheet      Current sheet
     * @param excelClass Current excel class
     * @param startIndex Excel header index
     * @param dataList   All data
     * @param collect    Whether collect the Java objects generated for each row when imported
     */
    private void reader(Sheet sheet, Class<R> excelClass, int startIndex, List<R> dataList, boolean collect) {
        R r;
        this.isSave = true;
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        boolean stop = false;
        List<ReadListener> rowReadListeners = this.readListenersMap.get(RowReadListener.class);
        Gson gson = new Gson();
        for (Row row : sheet) {
            if (stop) {
                break;
            }
            boolean hasNext = row.getRowNum() < sheet.getLastRowNum();
            if (row.getRowNum() < startIndex) {
                continue;
            }
            if (row.getRowNum() == startIndex) {
                if (this.headNameList.isEmpty()) {
                    for (Cell cell : row) {
                        headNameList.add(cell.getStringCellValue());
                        stop = ListenerChain.doReadRow(rowReadListeners, null, headNameList, startIndex, true, hasNext);
                    }
                }
                continue;
            }
            try {
                r = excelClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ExcelInitException("Excel entity init failure, " + e.getMessage());
            }
            for (int c = 0; c < row.getLastCellNum() && this.isSave; c++) {
                Field field = excelFieldMap.get(headNameList.get(c));
                if (field == null) {
                    throw new ExcelTemplateException();
                }
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                ExcelAssert excelAssert = field.getAnnotation(ExcelAssert.class);
                ExcelDataConvert excelDataConvert = field.getAnnotation(ExcelDataConvert.class);
                Cell valueCell = row.getCell(c);
                Object value;
                try {
                    if (valueCell != null) {
                        value = this.getValue(r, valueCell, field, excelField, gson, hasNext);
                        context.setVariable(field.getName(), value);
                        this.assertValue(parser, context, row, c, field, excelField, excelAssert);
                        value = this.convert(field, value, parser, excelDataConvert, context);
                        ListenerChain.doReadCell(rowReadListeners, r, value, field, row.getRowNum(), c, false);
                        if (isSave && value != null) {
                            this.setValue(r, field, value);
                        }
                    } else {
                        context.setVariable(field.getName(), null);
                        this.allowEmpty(r, field, excelField, row.getRowNum(), c, hasNext);
                        this.assertValue(parser, context, row, c, field, excelField, excelAssert);
                        value = this.convert(field, null, parser, excelDataConvert, context);
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
            if (this.isSave) {
                try {
                    ListenerChain.doReadRow(rowReadListeners, r, null, row.getRowNum(), false, hasNext);
                    if (collect) {
                        dataList.add(r);
                    }
                } catch (Exception e) {
                    throw new ExcelResolverException(e.getMessage());
                }
            }
        }
        ListenerChain.doResultNotify(this.readListenersMap.get(ResultReadListener.class), dataList);
    }

    /**
     * Get the value of the cell
     *
     * @param cell       cell
     * @param excelField Excel field of current field
     * @param field      Current field
     * @param gson       Gson
     * @param hasNext    Whether has next row
     * @param r          Current row generated row
     * @return value
     */
    private Object getValue(R r, Cell cell, Field field, ExcelField excelField, Gson gson, boolean hasNext) {
        switch (cell.getCellType()) {
            case _NONE:
            case BLANK:
            case ERROR:
                this.allowEmpty(r, field, excelField, cell.getRowIndex(), cell.getColumnIndex(), hasNext);
                break;
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return gson.fromJson(gson.toJson(cell.getNumericCellValue()), field.getType());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return cell.getStringCellValue();
        }
        return null;
    }

    /**
     * Data converter
     *
     * @param field            Current field
     * @param value            Attribute values
     * @param parser           El parser
     * @param excelDataConvert excelDataConvert
     * @param context          EL context
     * @return new value
     */
    private Object convert(Field field, Object value, ExpressionParser parser, ExcelDataConvert excelDataConvert, EvaluationContext context) {
        if (excelDataConvert != null && !"".equals(excelDataConvert.expr2())) {
            return parser.parseExpression(excelDataConvert.expr2()).getValue(context);
        }
        if (this.dataConvertMap != null) {
            DataConvert<?> dataConvert = this.dataConvertMap.get(field.getName());
            if (dataConvert != null) {
                return dataConvert.toEntityAttribute(value, field);
            }
        }
        return value;
    }

    /**
     * Set values for the fields of the object
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
            throw new IllegalArgumentException("Unsupported data type, you can use a data converter");
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
     * @param hasNext    Whether has next row
     */
    private void allowEmpty(R r, Field field, ExcelField excelField, int rowIndex, int colIndex, boolean hasNext) {
        if (excelField.allowEmpty()) {
            return;
        }
        this.isSave = ListenerChain.doReadEmpty(this.readListenersMap.get(EmptyReadListener.class), r, field, excelField, rowIndex, colIndex, hasNext);
    }

    /**
     * Cell value assert
     *
     * @param parser      El parser
     * @param context     EL context
     * @param row         Current row
     * @param c           Current col index
     * @param field       Current field
     * @param excelField  ExcelFiled annotation on current filed
     * @param excelAssert Excel Assert on current field
     */
    private void assertValue(ExpressionParser parser, EvaluationContext context, Row row, int c, Field field, ExcelField excelField, ExcelAssert excelAssert) {
        if (excelAssert != null) {
            Boolean test = parser.parseExpression(excelAssert.expr()).getValue(context, Boolean.class);
            if (test != null && !test) {
                throw new ExcelAssertException(excelAssert.message(), excelField, field, row.getRowNum(), c);
            }
        }
    }
}
