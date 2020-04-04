package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.convert.*;
import cn.gjing.tools.excel.exception.ExcelAssertException;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.exception.ExcelTemplateException;
import cn.gjing.tools.excel.listen.ReadCallback;
import cn.gjing.tools.excel.listen.ReadListener;
import cn.gjing.tools.excel.resolver.ExcelReaderResolver;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.valid.ExcelAssert;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.*;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Gjing
 **/
class DefaultExcelReadResolver<R> implements ExcelReaderResolver<R> {
    private Map<String, EnumConvert<? extends Enum<?>, ?>> enumConvertMap;
    private Map<String, Class<?>> enumInterfaceTypeMap;
    private List<String> headNameList;
    private Map<String, Field> excelFieldMap;
    private Map<String, DataConvert<?>> dataConvertMap;
    private List<R> dataList;
    private boolean isSave;

    public DefaultExcelReadResolver() {
        this.excelFieldMap = new HashMap<>(16);
        this.headNameList = new ArrayList<>();
        this.dataList = new ArrayList<>();
    }

    @Override
    public void read(InputStream inputStream, Class<R> excelClass, ReadListener<List<R>> readListener, int headerIndex, int readLines, String sheetName,
                     ReadCallback<R> callback, Workbook workbook, List<Field> excelFieldList, Excel excel) {
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
                    }).collect(Collectors.toMap(field -> field.getAnnotation(ExcelField.class).value(), field -> field));
        }
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new ExcelResolverException("The" + sheetName + " is not found in the workbook");
        }
        this.reader(sheet, excelClass, readListener, headerIndex, readLines, callback);
    }

    /**
     * Start read
     *
     * @param sheet        Current sheet
     * @param excelClass   Current excel class
     * @param readListener Read listener
     * @param headerIndex  Excel header index
     * @param readLines    Read lines
     * @param readCallback Read callback
     */
    private void reader(Sheet sheet, Class<R> excelClass, ReadListener<List<R>> readListener, int headerIndex, int readLines, ReadCallback<R> readCallback) {
        R o;
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        Gson gson = new Gson();
        int realReadLines = readLines + headerIndex;
        for (Row row : sheet) {
            this.isSave = true;
            if (row.getRowNum() < headerIndex) {
                continue;
            }
            if (row.getRowNum() == headerIndex) {
                if (this.headNameList.isEmpty()) {
                    for (Cell cell : row) {
                        headNameList.add(cell.getStringCellValue());
                    }
                }
                continue;
            }
            if (readLines != 0 && row.getRowNum() > realReadLines) {
                readCallback.currentData(this.dataList, row.getRowNum() - 1, false);
                break;
            }
            try {
                o = excelClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ExcelInitException("Excel model init failure, " + e.getMessage());
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
                try {
                    if (valueCell != null) {
                        Object value = this.getValue(valueCell, field, excelField, readCallback, gson);
                        context.setVariable(field.getName(), value);
                        this.assertValue(parser, context, row, c, field, excelField, excelAssert, value);
                        value = readCallback.readCol(value, field, row.getRowNum(), c);
                        value = this.changeData(field, value, parser, excelDataConvert, context);
                        if (this.isSave && value != null) {
                            this.setValue(o, field, value, gson);
                        }
                    } else {
                        Object value = this.changeData(field, null, parser, excelDataConvert, context);
                        if (value == null) {
                            this.valid(field, excelField, row.getRowNum(), c, readCallback);
                            this.assertValue(parser, context, row, c, field, excelField, excelAssert, null);
                            continue;
                        }
                        this.setValue(o, field, value, gson);
                    }
                } catch (Exception e) {
                    if (e instanceof ExcelAssertException) {
                        throw (ExcelAssertException) e;
                    }
                    throw new ExcelResolverException(e.getMessage());
                }
            }
            if (this.isSave) {
                try {
                    this.dataList.add(readCallback.readLine(o, row.getRowNum()));
                    readCallback.currentData(this.dataList, row.getRowNum(), row.getRowNum() < sheet.getLastRowNum());
                } catch (Exception e) {
                    throw new ExcelResolverException(e.getMessage());
                }
            }
        }
        readListener.notify(this.dataList);
    }

    /**
     * Get the value of the cell
     *
     * @param cell         cell
     * @param excelField   Excel field of current field
     * @param field        Current field
     * @param gson         Gson
     * @param readCallback read callback
     * @return value
     */
    private Object getValue(Cell cell, Field field, ExcelField excelField, ReadCallback<R> readCallback, Gson gson) {
        switch (cell.getCellType()) {
            case _NONE:
            case BLANK:
            case ERROR:
                this.valid(field, excelField, cell.getRowIndex(), cell.getColumnIndex(), readCallback);
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
    private Object changeData(Field field, Object value, ExpressionParser parser, ExcelDataConvert excelDataConvert, EvaluationContext context) {
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
     * @param gson  gson
     */
    private void setValue(R o, Field field, Object value, Gson gson) {
        if (field.getType().isEnum()) {
            if (value instanceof Enum) {
                BeanUtils.setFieldValue(o, field, value);
                return;
            }
            if (this.enumConvertMap == null) {
                this.enumConvertMap = new HashMap<>(16);
                this.enumInterfaceTypeMap = new HashMap<>(16);
            }
            EnumConvert<? extends Enum<?>, ?> enumConvert = this.enumConvertMap.get(field.getName());
            if (enumConvert == null) {
                ExcelEnumConvert excelEnumConvert = field.getAnnotation(ExcelEnumConvert.class);
                if (excelEnumConvert == null) {
                    return;
                }
                Class<?> interfaceType = BeanUtils.getInterfaceType(excelEnumConvert.convert(), EnumConvert.class, 1);
                try {
                    enumConvert = excelEnumConvert.convert().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new ExcelInitException("Enum convert init failure " + field.getName() + ", " + e.getMessage());
                }
                BeanUtils.setFieldValue(o, field, enumConvert.toEntityAttribute(gson.fromJson(gson.toJson(value), (java.lang.reflect.Type) interfaceType)));
                this.enumConvertMap.put(field.getName(), enumConvert);
                this.enumInterfaceTypeMap.put(field.getName(), interfaceType);
                return;
            }
            BeanUtils.setFieldValue(o, field, enumConvert.toEntityAttribute(gson.fromJson(gson.toJson(value), (java.lang.reflect.Type) enumInterfaceTypeMap.get(field.getName()))));
            return;
        }
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
            throw new IllegalArgumentException("Unsupported data type");
        }
    }

    /**
     * Check is not empty strategy
     *
     * @param field        Current field
     * @param excelField   ExcelFiled annotation on current filed
     * @param rowIndex     Current row index
     * @param colIndex     Current col index
     * @param readCallback Read callback
     */
    private void valid(Field field, ExcelField excelField, int rowIndex, int colIndex, ReadCallback<R> readCallback) {
        if (excelField.allowEmpty()) {
            return;
        }
        this.isSave = false;
        readCallback.readJump(field, excelField, rowIndex, colIndex);
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
     * @param value       Current attribute value
     */
    private void assertValue(ExpressionParser parser, EvaluationContext context, Row row, int c, Field field, ExcelField excelField, ExcelAssert excelAssert, Object value) {
        if (excelAssert != null) {
            Boolean test = parser.parseExpression(excelAssert.expr()).getValue(context, Boolean.class);
            if (test != null && !test) {
                throw new ExcelAssertException(excelAssert.message(), excelField, field, row.getRowNum(), c);
            }
        }
    }
}
