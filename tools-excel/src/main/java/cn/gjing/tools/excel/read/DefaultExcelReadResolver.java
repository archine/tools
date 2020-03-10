package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.*;
import cn.gjing.tools.excel.exception.ExcelDataValidException;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.exception.ExcelTemplateException;
import cn.gjing.tools.excel.listen.DataConvert;
import cn.gjing.tools.excel.listen.EnumConvert;
import cn.gjing.tools.excel.listen.ReadCallback;
import cn.gjing.tools.excel.listen.ReadListener;
import cn.gjing.tools.excel.resolver.ExcelReaderResolver;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Gjing
 **/
class DefaultExcelReadResolver<R> implements ExcelReaderResolver<R>, AutoCloseable {
    private Workbook workbook;
    private Sheet sheet;
    private int totalCol = 0;
    private InputStream inputStream;
    private Gson gson;
    private Map<String, EnumConvert<? extends Enum<?>, ?>> enumConvertMap;
    private Map<String, Class<?>> enumInterfaceTypeMap;
    private List<String> headNameList;
    private Map<String, Field> hasAnnotationFieldMap;
    private Map<String, DataConvert<?, ?>> dataConvertMap;
    private boolean isSave;

    public DefaultExcelReadResolver() {
        this.hasAnnotationFieldMap = new HashMap<>(16);
        this.headNameList = new ArrayList<>();
        this.gson = new Gson();
    }

    @Override
    public void read(InputStream inputStream, Class<R> excelClass, ReadListener<List<R>> readListener, int headerIndex, int readLines, String sheetName, ReadCallback<R> callback) {
        this.inputStream = inputStream;
        Excel excel = excelClass.getAnnotation(Excel.class);
        if (hasAnnotationFieldMap.isEmpty()) {
            List<Field> excelFields = BeanUtils.getExcelFields(excelClass, null);
            this.hasAnnotationFieldMap = excelFields.stream()
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
            switch (excel.type()) {
                case XLS:
                    try {
                        if (this.workbook == null) {
                            this.workbook = new HSSFWorkbook(inputStream);
                        }
                    } catch (Exception e) {
                        throw new ExcelInitException("Init workbook error, " + e.getMessage());
                    }
                    this.sheet = this.workbook.getSheet(sheetName);
                    this.reader(excelClass, readListener, headerIndex, readLines, callback);
                    break;
                case XLSX:
                    if (this.workbook == null) {
                        this.workbook = StreamingReader.builder().rowCacheSize(excel.maxSize()).bufferSize(excel.bufferSize()).open(inputStream);
                    }
                    this.sheet = this.workbook.getSheet(sheetName);
                    this.reader(excelClass, readListener, headerIndex, readLines, callback);
                    break;
                default:
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (this.inputStream != null) {
            this.inputStream.close();
        }
        if (this.workbook != null) {
            this.workbook.close();
        }
    }

    private void reader(Class<R> excelClass, ReadListener<List<R>> readListener, int headerIndex, int readLines, ReadCallback<R> readCallback) {
        List<R> dataList = new ArrayList<>();
        R o;
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        int realReadLines = readLines + headerIndex;
        for (Row row : sheet) {
            this.isSave = true;
            if (row.getRowNum() < headerIndex) {
                continue;
            }
            if (row.getRowNum() == headerIndex) {
                if (this.headNameList.isEmpty()) {
                    for (Cell cell : row) {
                        this.totalCol++;
                        headNameList.add(cell.getStringCellValue());
                    }
                }
                continue;
            }
            if (readLines != 0 && row.getRowNum() > realReadLines) {
                break;
            }
            try {
                o = excelClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ExcelInitException("Excel model init failure, " + e.getMessage());
            }
            for (int c = 0; c < totalCol && this.isSave; c++) {
                Field field = hasAnnotationFieldMap.get(headNameList.get(c));
                if (field == null) {
                    throw new ExcelTemplateException();
                }
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                ExcelAssert excelAssert = field.getAnnotation(ExcelAssert.class);
                Cell valueCell = row.getCell(c);
                try {
                    if (valueCell != null) {
                        Object value = this.getValue(valueCell, field, excelField, readCallback);
                        if (this.dataConvertMap != null) {
                            DataConvert<?, ?> dataConvert = this.dataConvertMap.get(field.getName());
                            if (dataConvert != null) {
                                value = dataConvert.toEntityAttribute(value, field, excelField);
                            }
                        }
                        this.assertValue(parser, context, row, c, field, excelField, excelAssert, value);
                        if (this.isSave && value != null) {
                            this.setValue(o, field, value);
                        }
                    } else {
                        this.assertValue(parser, context, row, c, field, excelField, excelAssert, null);
                        this.valid(field, excelField, row.getRowNum(), c, readCallback);
                    }
                } catch (Exception e) {
                    if (e instanceof ExcelDataValidException) {
                        throw (ExcelDataValidException) e;
                    }
                    throw new ExcelResolverException(e.getMessage());
                }
            }
            if (this.isSave) {
                try {
                    dataList.add(readCallback.readLine(o, row.getRowNum()));
                } catch (Exception e) {
                    throw new ExcelResolverException(e.getMessage());
                }
            }
        }
        readListener.notify(dataList);
    }

    /**
     * Gets the value of the cell
     *
     * @param cell cell
     * @return value
     */
    private Object getValue(Cell cell, Field field, ExcelField excelField, ReadCallback<R> readCallback) {
        Object value = null;
        switch (cell.getCellType()) {
            case _NONE:
            case BLANK:
            case ERROR:
                this.valid(field, excelField, cell.getRowIndex(), cell.getColumnIndex(), readCallback);
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    numberFormat.setMinimumFractionDigits(0);
                    numberFormat.setGroupingUsed(false);
                    value = numberFormat.format(cell.getNumericCellValue());
                }
                break;
            case FORMULA:
                value = cell.getCellFormula();
                break;
            default:
                value = cell.getStringCellValue();
                break;
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
        if (field.getType().isEnum()) {
            if (this.enumConvertMap == null) {
                this.enumConvertMap = new HashMap<>(16);
                this.enumInterfaceTypeMap = new HashMap<>(16);
            }
            EnumConvert<? extends Enum<?>, ?> enumConvert = this.enumConvertMap.get(field.getName());
            if (enumConvert == null) {
                ExcelEnumConvert excelEnumConvert = field.getAnnotation(ExcelEnumConvert.class);
                ParamUtils.requireNonNull(excelEnumConvert, field.getName() + " was not found enum convert");
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
            BeanUtils.setFieldValue(o, field, gson.fromJson(gson.toJson(value), field.getType()));
        } catch (JsonSyntaxException e) {
            if (value instanceof Temporal) {
                BeanUtils.setFieldValue(o, field, value);
                return;
            }
            if (field.getType() == LocalDateTime.class) {
                BeanUtils.setFieldValue(o, field, LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault()));
                return;
            }
            if (field.getType() == LocalDate.class) {
                BeanUtils.setFieldValue(o, field, LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault()).toLocalDate());
            }
        }
    }

    private void valid(Field field, ExcelField excelField, int rowIndex, int colIndex, ReadCallback<R> readCallback) {
        if (excelField.allowEmpty()) {
            return;
        }
        switch (excelField.strategy()) {
            case JUMP:
                this.isSave = false;
                readCallback.readJump(field, excelField, rowIndex, colIndex);
                break;
            case ERROR:
                throw new ExcelResolverException(excelField.message());
            default:
        }
    }

    private void assertValue(ExpressionParser parser, EvaluationContext context, Row row, int c, Field field, ExcelField excelField, ExcelAssert excelAssert, Object value) {
        if (excelAssert != null) {
            context.setVariable(field.getName(), value);
            Boolean test = parser.parseExpression(excelAssert.expr()).getValue(context, Boolean.class);
            if (test != null && !test) {
                throw new ExcelDataValidException(excelAssert.message(), excelField, field, row.getRowNum(), c);
            }
        }
    }
}
