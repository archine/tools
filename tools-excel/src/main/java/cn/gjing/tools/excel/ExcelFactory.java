package cn.gjing.tools.excel;

import cn.gjing.tools.excel.read.ExcelReader;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.ExcelWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

/**
 * Excel factory
 *
 * @author Gjing
 **/
public class ExcelFactory {

    private ExcelFactory() {

    }

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
        ParamUtils.requireNonNull(excel, "@Excel annotation was not found on the " + excelClass);
        List<Field> hasExcelFieldList = BeanUtils.getExcelFields(excelClass, ignores);
        return new ExcelWriter(fileName == null ? "".equals(excel.value()) ? UUID.randomUUID().toString().replaceAll("-", "") : excel.value() : fileName,
                excel, response, hasExcelFieldList);
    }

    /**
     * Create an Excel read
     *
     * @param inputStream Excel file inputStream
     * @param excelClass  Excel mapped entity
     * @param <R>         Entity type
     * @return ExcelReader
     */
    public static <R> ExcelReader<R> createReader(InputStream inputStream, Class<R> excelClass) {
        ParamUtils.requireNonNull(excelClass.getAnnotation(Excel.class), "@Excel annotation was not found on the " + excelClass);
        return new ExcelReader<>(excelClass, inputStream);
    }

}
