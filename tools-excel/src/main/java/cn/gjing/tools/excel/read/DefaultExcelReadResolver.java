package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.*;
import cn.gjing.tools.excel.resolver.ExcelReaderResolver;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.valid.DateValid;
import com.google.gson.Gson;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Gjing
 **/
class DefaultExcelReadResolver implements ExcelReaderResolver,AutoCloseable {
    private Workbook workbook;
    private Sheet sheet;
    private Map<String, Field> hasAnnotationFieldMap = new HashMap<>(16);
    private List<String> headNameList = new ArrayList<>();
    private int totalCol = 0;
    private InputStream inputStream;
    private Gson gson = new Gson();
    private Map<String, SimpleDateFormat> formatMap = new HashMap<>(16);
    private boolean n;

    @Override
    public void read(InputStream inputStream, Class<?> excelClass, Listener<List<Object>> listener, int headerIndex, int endIndex, String sheetName) {
        this.inputStream = inputStream;
        Excel excel = excelClass.getAnnotation(Excel.class);
        if (excel == null) {
            throw new NullPointerException("@Excel was not found on the excelClass");
        }
        if (hasAnnotationFieldMap.isEmpty()) {
            this.hasAnnotationFieldMap = Arrays.stream(excelClass.getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(ExcelField.class))
                    .collect(
                            Collectors.toMap(field -> field.getAnnotation(ExcelField.class).value(), field -> field)
                    );
            Class<?> superclass = excelClass.getSuperclass();
            if (superclass != Object.class) {
                Map<String, Field> supperFieldMap = Arrays.stream(superclass.getDeclaredFields())
                        .filter(f -> f.isAnnotationPresent(ExcelField.class))
                        .collect(
                                Collectors.toMap(field -> field.getAnnotation(ExcelField.class).value(), field -> field)
                        );
                this.hasAnnotationFieldMap.putAll(supperFieldMap);
            }
        }
        switch (excel.type()) {
            case XLS:
                try {
                    if (this.workbook == null) {
                        this.workbook = new HSSFWorkbook(inputStream);
                        this.sheet = this.workbook.getSheet(sheetName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.reader(excelClass, listener, headerIndex, endIndex);
                break;
            case XLSX:
                if (this.workbook == null) {
                    this.workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(inputStream);
                    this.sheet = this.workbook.getSheet(sheetName);
                }
                this.reader(excelClass, listener, headerIndex, endIndex);
                break;
            default:
                throw new NullPointerException("Doc type was not found");
        }
    }

    @Override
    public void close() throws IOException {
        if (this.inputStream != null) {
            this.inputStream.close();
        }
    }

    private void reader(Class<?> excelClass, Listener<List<Object>> listener, int headerIndex, int endIndex) {
        List<Object> dataList = new ArrayList<>();
        Object o = null;
        ExcelField excelField;
        Field field;
        Cell valueCell;
        String value;
        for (Row row : sheet) {
            this.n = true;
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
            if (endIndex != 0 && endIndex == row.getRowNum()) {
                break;
            }
            try {
                o = excelClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            for (int c = 0; c < totalCol && this.n; c++) {
                field = hasAnnotationFieldMap.get(headNameList.get(c));
                if (field == null) {
                    continue;
                }
                excelField = field.getAnnotation(ExcelField.class);
                valueCell = row.getCell(c);
                if (valueCell != null) {
                    value = this.getValue(valueCell, field, excelField);
                    if (this.n) {
                        this.setValue(o, field, value);
                    }
                } else {
                    this.valid(excelField);
                }
            }
            if (this.n) {
                dataList.add(o);
            }
        }
        listener.notify(dataList);
    }

    /**
     * Gets the value of the cell
     *
     * @param cell cell
     * @return value
     */
    private String getValue(Cell cell, Field field, ExcelField excelField) {
        Object value = "";
        switch (cell.getCellType()) {
            case _NONE:
            case BLANK:
            case ERROR:
                this.valid(excelField);
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    DateValid valid = field.getAnnotation(DateValid.class);
                    SimpleDateFormat format = this.formatMap.get(field.getName());
                    if (format == null) {
                        format = new SimpleDateFormat(valid == null ? "yyyy-MM-dd HH:mm:ss" : valid.pattern());
                        this.formatMap.put(field.getName(), format);
                    }
                    value = format.format(cell.getDateCellValue());
                } else {
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    numberFormat.setMaximumFractionDigits(10);
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
        return value.toString();
    }

    /**
     * Sets values for the fields of the object
     *
     * @param o          object
     * @param field      field
     * @param value      value
     */
    private void setValue(Object o, Field field, String value) {
        if (field.getType().isEnum()) {
            ExcelEnumConvert excelEnumConvert = field.getAnnotation(ExcelEnumConvert.class);
            Objects.requireNonNull(excelEnumConvert, "Enum convert cannot be null");
            Class<?> interfaceType = BeanUtils.getInterfaceType(excelEnumConvert.convert(), EnumConvert.class, 1);
            try {
                EnumConvert<? extends Enum<?>, ?> enumConvert = excelEnumConvert.convert().newInstance();
                this.setField(field, o, enumConvert.toEntityAttribute(gson.fromJson(value, (java.lang.reflect.Type) interfaceType)));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            this.setField(field, o, gson.fromJson(gson.toJson(value), field.getType()));
        }
    }

    /**
     * Set field value
     *
     * @param field field
     * @param o     object
     * @param value value
     */
    private void setField(Field field, Object o, Object value) {
        BeanUtils.setFieldValue(o, field, value);
    }

    private void valid(ExcelField excelField) {
        if (excelField.allowEmpty()) {
            return;
        }
        switch (excelField.strategy()) {
            case JUMP:
                this.n = false;
                break;
            case ERROR:
                throw new NullPointerException(excelField.message());
            default:
        }
    }
}
