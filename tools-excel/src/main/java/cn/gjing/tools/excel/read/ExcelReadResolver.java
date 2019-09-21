package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.Convert;
import cn.gjing.tools.excel.EnumConvert;
import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.resolver.ExcelReaderResolver;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.util.TimeUtils;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Gjing
 **/
class ExcelReadResolver implements ExcelReaderResolver {
    /**
     * 工作簿
     */
    private Workbook workbook;

    /**
     * 输入流
     */
    private InputStream inputStream;

    /**
     * sheet
     */
    private Sheet sheet;

    /**
     * 存放带有ExcelField注解的字段
     */
    private Map<String, Field> hasAnnotationFieldMap = new HashMap<>();

    @Override
    public ExcelReaderResolver builder(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    @Override
    public void read(Class<?> excelClass, Consumer<List<Object>> acceptList) {
        //得到excel的class实体
        Excel excel = excelClass.getAnnotation(Excel.class);
        if (excel == null) {
            throw new NullPointerException("@Excel was not found on the excelClass");
        }
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
        switch (excel.type()) {
            case XLS:
                try {
                    this.workbook = new HSSFWorkbook(this.inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //得到sheet
                this.sheet = this.workbook.getSheetAt(0);
                reader(excelClass, acceptList);
                break;
            case XLSX:
                this.workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(this.inputStream);
                this.sheet = this.workbook.getSheetAt(0);
                reader(excelClass, acceptList);
                break;
            default:
                throw new NullPointerException("Doc type was not found");
        }
    }

    private void reader(Class<?> excelClass, Consumer<List<Object>> consumer) {
        List<Object> dataList = new ArrayList<>();
        //excel列表头名称
        List<String> headNameList = new ArrayList<>();
        //获取excel列表头这行, 并设置总列数和列表头名称
        int totalCell = 0;
        //创建个新实例
        Object o = null;
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                //增加列表头名称和总列数
                for (Cell cell : row) {
                    totalCell++;
                    headNameList.add(cell.getStringCellValue());
                }
                continue;
            }
            try {
                o = excelClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            for (int c = 0; c < totalCell; c++) {
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
        consumer.accept(dataList);
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
                    : TimeUtils.stringToDate(value, excelField.pattern()));
            return;
        }
        if (field.getType().isEnum()) {
            Convert convert = field.getAnnotation(Convert.class);
            Objects.requireNonNull(convert, "Enum convert cannot be null");
            try {
                EnumConvert enumConvert = convert.convert().newInstance();
                this.setField(field, o, enumConvert.toEntityAttribute(value));
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
