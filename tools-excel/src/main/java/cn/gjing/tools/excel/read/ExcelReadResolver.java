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
        //得到excel的class实体
        Excel excel = excelClass.getAnnotation(Excel.class);
        if (excel == null) {
            throw new NullPointerException("@Excel was not found on the excelClass");
        }
        if (hasAnnotationFieldMap.isEmpty()) {
            //拿到所有带有注解的字段，放进map中
            this.hasAnnotationFieldMap = Arrays.stream(excelClass.getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(ExcelField.class))
                    .collect(
                            Collectors.toMap(field -> field.getAnnotation(ExcelField.class).value(), field -> field)
                    );
            //找找是否有父类，有就加进来
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
                        //得到sheet
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
        //创建个新实例
        Object o = null;
        for (Row row : sheet) {
            if (row.getRowNum() < headerIndex) {
                continue;
            }
            if (row.getRowNum() == headerIndex) {
                if (this.headNameList.isEmpty()) {
                    //增加列表头名称和总列数
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
     * 获取值
     *
     * @param cell 单元格
     * @return string
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
     * 给对象的字段设置值
     *
     * @param o          具体对象
     * @param field      字段
     * @param value      excel读出来的值
     * @param excelField excel字段注解
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
     * 字段set值
     *
     * @param field 字段
     * @param o     字段所在对象
     * @param value 值
     */
    private void setField(Field field, Object o, Object value) {
        BeanUtils.setFieldValue(o, field, value);
    }
}
