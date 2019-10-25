package cn.gjing.tools.excel;

import cn.gjing.tools.excel.read.ExcelReader;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.write.ExcelWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * Excel factory
 *
 * @author Gjing
 **/
public class ExcelFactory {

    /**
     * Create an Excel write
     *
     * @param excelClass Excel mapped entity
     * @param response   response
     * @param ignores    The exported field is to be ignored
     * @return ExcelWriter
     */
    public static ExcelWriter createWriter(Class<?> excelClass, HttpServletResponse response, String... ignores) {
        return createWriter(null, excelClass, response, ignores);
    }

    /**
     * Create an Excel write
     *
     * @param fileName   Excel file name，The priority is higher than the annotation specification
     * @param excelClass Excel mapped entity
     * @param response   response
     * @param ignores    The exported field is to be ignored
     * @return ExcelWriter
     */
    public static ExcelWriter createWriter(String fileName, Class<?> excelClass, HttpServletResponse response, String... ignores) {
        Excel excel = excelClass.getAnnotation(Excel.class);
        Objects.requireNonNull(excel, "@Excel was not found on the excelClass");
        List<Field> hasExcelFieldList = BeanUtils.getFields(excelClass, ignores);
        return new ExcelWriter(fileName == null ? excel.value() : fileName, excel, response, hasExcelFieldList);
    }

    /**
     * Create an Excel read
     *
     * @param inputStream Excel file inputStream
     * @param excelClass  Excel mapped entity
     * @param <T>         Entity type
     * @return ExcelReader
     */
    public static <T> ExcelReader<T> createReader(InputStream inputStream, Class<T> excelClass) {
        return new ExcelReader<>(excelClass, inputStream);
    }

}
