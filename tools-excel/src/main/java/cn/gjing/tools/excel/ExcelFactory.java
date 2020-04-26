package cn.gjing.tools.excel;

import cn.gjing.tools.excel.read.ExcelReaderContext;
import cn.gjing.tools.excel.read.resolver.ExcelReader;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.resolver.ExcelWriter;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel factory
 *
 * @author Gjing
 **/
public final class ExcelFactory {

    private ExcelFactory() {

    }

    /**
     * Create an excel writer
     *
     * @param excelClass Excel mapped entity
     * @param response   response
     * @param ignores    The exported field is to be ignored
     * @return ExcelWriter
     */
    public static ExcelWriter createWriter(Class<?> excelClass, HttpServletResponse response, String... ignores) {
        return createWriter(null, excelClass, response, true, ignores);
    }

    /**
     * Create an excel writer
     *
     * @param excelClass       Excel mapped entity
     * @param response         response
     * @param ignores          The exported field is to be ignored
     * @param initDefaultStyle Whether init  default excel style
     * @return ExcelWriter
     */
    public static ExcelWriter createWriter(Class<?> excelClass, HttpServletResponse response, boolean initDefaultStyle, String... ignores) {
        return createWriter(null, excelClass, response, initDefaultStyle, ignores);
    }

    /**
     * Create an excel writer
     *
     * @param excelClass Excel mapped entity
     * @param response   response
     * @param ignores    The exported field is to be ignored
     * @param fileName   Excel file name，The priority is higher than the annotation specification
     * @return ExcelWriter
     */
    public static ExcelWriter createWriter(String fileName, Class<?> excelClass, HttpServletResponse response, String... ignores) {
        return createWriter(fileName, excelClass, response, true, ignores);
    }

    /**
     * Create an excel writer
     *
     * @param fileName         Excel file name，The priority is higher than the annotation specification
     * @param excelClass       Excel mapped entity
     * @param response         response
     * @param ignores          The exported field is to be ignored
     * @param initDefaultStyle Whether init  default excel style
     * @return ExcelWriter
     */
    public static ExcelWriter createWriter(String fileName, Class<?> excelClass, HttpServletResponse response, boolean initDefaultStyle, String... ignores) {
        Excel excel = excelClass.getAnnotation(Excel.class);
        ParamUtils.requireNonNull(excel, "@Excel annotation was not found on the " + excelClass);
        List<String[]> headerArr = new ArrayList<>();
        ExcelWriterContext context = ExcelWriterContext.builder()
                .excelFields(BeanUtils.getExcelFields(excelClass, ignores, headerArr))
                .headNames(headerArr)
                .fileName(fileName == null ? "".equals(excel.value()) ? LocalDate.now().toString() : excel.value() : fileName)
                .build();
        return new ExcelWriter(context, excel, response, initDefaultStyle);
    }

    /**
     * Create an Excel reader
     *
     * @param file       Excel file
     * @param excelClass Excel mapped entity
     * @param ignores    Ignored header
     * @param <R>        Entity type
     * @return ExcelReader
     * @throws IOException io
     */
    public static <R> ExcelReader<R> createReader(MultipartFile file, Class<R> excelClass, String... ignores) throws IOException {
        return createReader(file.getInputStream(), excelClass, ignores);
    }

    /**
     * Create an Excel reader
     *
     * @param file       Excel file
     * @param excelClass Excel mapped entity
     * @param ignores    Ignored header
     * @param <R>        Entity type
     * @return ExcelReader
     * @throws IOException io
     */
    public static <R> ExcelReader<R> createReader(File file, Class<R> excelClass, String... ignores) throws IOException {
        return createReader(new FileInputStream(file), excelClass, ignores);
    }

    /**
     * Create an Excel reader
     *
     * @param inputStream Excel file inputStream
     * @param excelClass  Excel mapped entity
     * @param ignores     Ignored header
     * @param <R>         Entity type
     * @return ExcelReader
     */
    public static <R> ExcelReader<R> createReader(InputStream inputStream, Class<R> excelClass, String... ignores) {
        Excel excel = excelClass.getAnnotation(Excel.class);
        ParamUtils.requireNonNull(excel, "@Excel annotation was not found on the " + excelClass);
        List<Field> excelFieldList = BeanUtils.getExcelFields(excelClass, ignores, null);
        ExcelReaderContext<R> readerContext = new ExcelReaderContext<>(inputStream, excelClass, excelFieldList);
        return new ExcelReader<>(readerContext, excel);
    }
}
