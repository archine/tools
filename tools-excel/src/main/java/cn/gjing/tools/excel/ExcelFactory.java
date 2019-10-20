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
 * Excel读写工厂
 *
 * @author Gjing
 **/
public class ExcelFactory {

    /**
     * 创建excel读
     *
     * @param excelClass 导出的这个Excel对应的Class
     * @param response   response
     * @param ignores    忽略导出的字段
     * @return ExcelWriter
     */
    public static ExcelWriter createWriter(Class<?> excelClass, HttpServletResponse response, String... ignores) {
        return createWriter(null, excelClass, response, ignores);
    }

    /**
     * create excel write
     *
     * @param fileName   Excel文件名，优先级高于注解
     * @param excelClass 导出的这个Excel对应的Class
     * @param response   response
     * @param ignores    忽略导出的字段
     * @return ExcelWriter
     */
    public static ExcelWriter createWriter(String fileName, Class<?> excelClass, HttpServletResponse response, String... ignores) {
        Excel excel = excelClass.getAnnotation(Excel.class);
        Objects.requireNonNull(excel, "@Excel was not found on the excelClass");
        List<Field> hasExcelFieldList = BeanUtils.getFields(excelClass, ignores);
        return new ExcelWriter(fileName == null ? excel.value() : fileName, excel, response, hasExcelFieldList);
    }

    /**
     * 创建Excel reader
     *
     * @param inputStream excel文件输入流
     * @param excelClass  导出的这个Excel对应的Class
     * @param <T>         返回的实体类型
     * @return ExcelReader
     */
    public static <T> ExcelReader<T> createReader(InputStream inputStream, Class<T> excelClass) {
        return new ExcelReader<>(excelClass, inputStream);
    }

}
