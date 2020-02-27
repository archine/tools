package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.*;
import cn.gjing.tools.excel.resolver.ExcelReaderResolver;
import cn.gjing.tools.excel.util.BeanUtils;
import com.google.gson.Gson;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

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
class DefaultExcelReadResolver<R> implements ExcelReaderResolver<R>, AutoCloseable {
    private Workbook workbook;
    private Sheet sheet;
    private int totalCol = 0;
    private InputStream inputStream;
    private Gson gson;
    private Map<String, SimpleDateFormat> formatMap;
    private Map<String, EnumConvert<? extends Enum<?>, ?>> enumConvertMap;
    private Map<String, Class<?>> enumInterfaceTypeMap;
    private List<String> headNameList;
    private Map<String, Field> hasAnnotationFieldMap;
    private boolean isSave;

    public DefaultExcelReadResolver() {
        this.hasAnnotationFieldMap = new HashMap<>(16);
        this.headNameList =new ArrayList<>();
        this.gson = new Gson();
    }

    @Override
    public void read(InputStream inputStream, Class<R> excelClass, ReadListener<List<R>> readListener, int headerIndex, int readLines, String sheetName, ReadCallback<R> callback) {
        this.inputStream = inputStream;
        Excel excel = excelClass.getAnnotation(Excel.class);
        if (hasAnnotationFieldMap.isEmpty()) {
            List<Field> excelFields = BeanUtils.getExcelFields(excelClass, null);
            this.hasAnnotationFieldMap = excelFields.stream()
                    .collect(Collectors.toMap(field -> field.getAnnotation(ExcelField.class).value(), field -> field));
            switch (excel.type()) {
                case XLS:
                    try {
                        if (this.workbook == null) {
                            this.workbook = new HSSFWorkbook(inputStream);
                        }
                        this.sheet = this.workbook.getSheet(sheetName);
                        this.reader(excelClass, readListener, headerIndex, readLines, callback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case XLSX:
                    if (this.workbook == null) {
                        this.workbook = StreamingReader.builder().rowCacheSize(excel.maxSize()).bufferSize(excel.bufferSize()).open(inputStream);
                    }
                    this.sheet = this.workbook.getSheet(sheetName);
                    try {
                        this.reader(excelClass, readListener, headerIndex, readLines, callback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    throw new NullPointerException("Excel type was not found");
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (this.inputStream != null) {
            this.inputStream.close();
        }
    }

    private void reader(Class<R> excelClass, ReadListener<List<R>> readListener, int headerIndex, int readLines, ReadCallback<R> readCallback) {
        List<R> dataList = new ArrayList<>();
        R o = null;
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
                e.printStackTrace();
            }
            for (int c = 0; c < totalCol && this.isSave; c++) {
                Field field = hasAnnotationFieldMap.get(headNameList.get(c));
                if (field == null) {
                    continue;
                }
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                Cell valueCell = row.getCell(c);
                if (valueCell != null) {
                    Object value = this.getValue(valueCell, field, excelField, readCallback);
                    if (this.isSave && value != null) {
                        this.setValue(o, field, value);
                    }
                } else {
                    this.valid(field, excelField, row.getRowNum(), c, readCallback);
                }
            }
            if (this.isSave) {
                dataList.add(readCallback.readLine(o, row.getRowNum()));
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
                    if (this.formatMap == null) {
                        this.formatMap = new HashMap<>(16);
                        SimpleDateFormat format = new SimpleDateFormat(excelField.pattern());
                        this.formatMap.put(field.getName(), format);
                        value = format.format(cell.getDateCellValue());
                    } else {
                        SimpleDateFormat format = this.formatMap.get(field.getName());
                        if (format == null) {
                            format = new SimpleDateFormat(excelField.pattern());
                            this.formatMap.put(field.getName(), format);
                        }
                        value = format.format(cell.getDateCellValue());
                    }
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
     * Sets values for the fields of the object
     *
     * @param o     object
     * @param field field
     * @param value value
     */
    private void setValue(R o, Field field, Object value) {
        if (field.getType().isEnum()) {
            if (this.enumConvertMap == null) {
                this.enumConvertMap = new HashMap<>(16);
            }
            if (this.enumInterfaceTypeMap == null) {
                this.enumInterfaceTypeMap = new HashMap<>(16);
            }
            EnumConvert<? extends Enum<?>, ?> enumConvert = this.enumConvertMap.get(field.getName());
            if (enumConvert == null) {
                ExcelEnumConvert excelEnumConvert = field.getAnnotation(ExcelEnumConvert.class);
                Objects.requireNonNull(excelEnumConvert, field.getName() + " was not found enum converter");
                Class<?> interfaceType = BeanUtils.getInterfaceType(excelEnumConvert.convert(), EnumConvert.class, 1);
                try {
                    enumConvert = excelEnumConvert.convert().newInstance();
                    BeanUtils.setFieldValue(o, field, enumConvert.toEntityAttribute(gson.fromJson(gson.toJson(value), (java.lang.reflect.Type) interfaceType)));
                    this.enumConvertMap.put(field.getName(), enumConvert);
                    this.enumInterfaceTypeMap.put(field.getName(), interfaceType);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                return;
            }
            BeanUtils.setFieldValue(o, field, enumConvert.toEntityAttribute(gson.fromJson(gson.toJson(value), (java.lang.reflect.Type) enumInterfaceTypeMap.get(field.getName()))));
        } else {
            BeanUtils.setFieldValue(o, field, gson.fromJson(gson.toJson(value), field.getType()));
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
                throw new NullPointerException(excelField.message());
            default:
        }
    }
}
