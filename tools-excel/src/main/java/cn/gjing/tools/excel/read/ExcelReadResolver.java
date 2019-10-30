package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.*;
import cn.gjing.tools.excel.resolver.ExcelReaderResolver;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.util.TimeUtils;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Gjing
 **/
class ExcelReadResolver implements ExcelReaderResolver {
    private Workbook workbook;
    private Sheet sheet;
    private Map<String, Field> hasAnnotationFieldMap = new HashMap<>();
    private List<String> headNameList = new ArrayList<>();
    private int totalCol = 0;
    private static Gson gson = new Gson();

    @Override
    public void read(InputStream inputStream, Class<?> excelClass, Listener<List<Object>> listener, int headerIndex, int endIndex, String sheetName) {
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
            Class superclass = excelClass.getSuperclass();
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

    private void reader(Class<?> excelClass, Listener<List<Object>> listener, int headerIndex, int endIndex) {
        List<Object> dataList = new ArrayList<>();
        Object o = null;
        for (Row row : sheet) {
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
            for (int c = 0; c < totalCol; c++) {
                Field field = hasAnnotationFieldMap.get(headNameList.get(c));
                if (field == null) {
                    continue;
                }
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                Cell cell = row.getCell(c);
                if (cell != null) {
                    this.setValue(o, field, this.getValue(cell), excelField);
                }
            }
            dataList.add(o);
        }
        listener.notify(dataList);
    }

    /**
     * Gets the value of the cell
     *
     * @param cell cell
     * @return value
     */
    private String getValue(Cell cell) {
        Object value = "";
        switch (cell.getCellType()) {
            case _NONE:
            case BLANK:
            case ERROR:
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    value = TimeUtils.dateToString(cell.getDateCellValue(), "yyyy-MM-dd HH:mm:ss");
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
     * @param excelField Excel list headers map to entity fields annotations
     */
    @SuppressWarnings("unchecked")
    private void setValue(Object o, Field field, String value, ExcelField excelField) {
        if (field.getType() == String.class) {
            this.setField(field, o, value);
            return;
        }
        if (field.getType() == int.class || field.getType() == Integer.class) {
            this.setField(field, o, Integer.parseInt(value));
            return;
        }
        if (field.getType() == long.class || field.getType() == Long.class) {
            this.setField(field, o, Long.parseLong(value));
            return;
        }
        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            this.setField(field, o, Boolean.parseBoolean(value));
            return;
        }
        if (field.getType() == Date.class) {
            this.setField(field, o, ParamUtils.equals("", excelField.pattern())
                    ? TimeUtils.stringToDate(value)
                    : TimeUtils.stringToDate(value, excelField.pattern())
            );
            return;
        }
        if (field.getType().isEnum()) {
            ExcelEnumConvert excelEnumConvert = field.getAnnotation(ExcelEnumConvert.class);
            Objects.requireNonNull(excelEnumConvert, "Enum convert cannot be null");
            Class<?> interfaceType = BeanUtils.getInterfaceType(excelEnumConvert.convert(), EnumConvert.class, 1);
            try {
                EnumConvert enumConvert = excelEnumConvert.convert().newInstance();
                this.setField(field, o, enumConvert.toEntityAttribute(gson.fromJson(value, interfaceType)));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return;
        }
        if (field.getType() == double.class || field.getType() == Double.class) {
            this.setField(field, o, Double.parseDouble(value));
            return;
        }
        if (field.getType() == float.class || field.getType() == Float.class) {
            this.setField(field, o, Float.parseFloat(value));
            return;
        }
        if (field.getType() == byte.class || field.getType() == Byte.class) {
            this.setField(field, o, Byte.parseByte(value));
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
}
