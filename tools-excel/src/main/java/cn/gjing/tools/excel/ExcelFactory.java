package cn.gjing.tools.excel;

import cn.gjing.tools.excel.read.ExcelReader;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.ExcelWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        return createWriter(null, excelClass, response, null, ignores);
    }

    /**
     * 创建excel读
     *
     * @param excelClass     导出的这个Excel对应的Class
     * @param response       response
     * @param ignores        忽略导出的字段
     * @param excelClassList 导出的这个Excel对应实体的集合
     * @return ExcelWriter
     */
    public static ExcelWriter createWriter(Class<?> excelClass, HttpServletResponse response, List<?> excelClassList, String... ignores) {
        return createWriter(null, excelClass, response, excelClassList, ignores);
    }

    /**
     * create excel write
     *
     * @param fileName       Excel文件名，优先级高于注解
     * @param excelClass     导出的这个Excel对应的Class
     * @param response       response
     * @param ignores        忽略导出的字段
     * @param excelClassList 导出的这个Excel对应实体的集合
     * @return ExcelWriter
     */
    public static ExcelWriter createWriter(String fileName, Class<?> excelClass, HttpServletResponse response, List<?> excelClassList, String... ignores) {
        Excel excelClassAnnotation = excelClass.getAnnotation(Excel.class);
        Objects.requireNonNull(excelClassAnnotation, "@Excel was not found on the excelClass");
        //Get all the declared fields
        Field[] declaredFields = excelClass.getDeclaredFields();
        //找到所有带有@ExcelField注解且不为过滤的字段
        List<Field> hasExcelFieldList = Arrays.stream(declaredFields)
                .filter(e -> e.isAnnotationPresent(ExcelField.class))
                .filter(e -> !ParamUtils.contains(ignores, e.getName()))
                .collect(Collectors.toList());
        //如果有父类，父类也加进来
        Class<?> superclass = excelClass.getSuperclass();
        if (superclass != Object.class) {
            hasExcelFieldList.addAll(Arrays.stream(superclass.getDeclaredFields())
                    .filter(e -> e.isAnnotationPresent(ExcelField.class))
                    .filter(e -> !ParamUtils.contains(ignores, e.getName()))
                    .collect(Collectors.toList()));
        }
        //获取excel表头
        List<String> headers = hasExcelFieldList.stream()
                .map(e -> e.getAnnotation(ExcelField.class).value())
                .collect(Collectors.toList());
        return new ExcelWriter(fileName == null ? excelClassAnnotation.value() : fileName, excelClassAnnotation, headers, response, excelClassList, hasExcelFieldList);
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
