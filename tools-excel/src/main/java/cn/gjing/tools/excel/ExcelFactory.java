package cn.gjing.tools.excel;

import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.metadata.ExcelType;
import cn.gjing.tools.excel.read.ExcelReaderContext;
import cn.gjing.tools.excel.read.resolver.ExcelReader;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.resolver.ExcelBindWriter;
import cn.gjing.tools.excel.write.resolver.ExcelSimpleWriter;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
     * @param ignores    Which table heads to be ignored when exporting, in the case of multiple table heads,
     *                   there are more than one child table heads under the ignored table head,
     *                   then the child table head will be ignored, if the ignored table head is from the table head
     *                   then it is ignored
     * @return ExcelWriter
     */
    public static ExcelBindWriter createWriter(Class<?> excelClass, HttpServletResponse response, String... ignores) {
        return createWriter(null, excelClass, response, true, ignores);
    }

    /**
     * Create an excel writer
     *
     * @param excelClass       Excel mapped entity
     * @param response         response
     * @param ignores          Which table heads to be ignored when exporting, in the case of multiple table heads,
     *                         there are more than one child table heads under the ignored table head,
     *                         then the child table head will be ignored, if the ignored table head is from the table head
     *                         then it is ignored
     * @param initDefaultStyle Whether init  default excel style
     * @return ExcelWriter
     */
    public static ExcelBindWriter createWriter(Class<?> excelClass, HttpServletResponse response, boolean initDefaultStyle, String... ignores) {
        return createWriter(null, excelClass, response, initDefaultStyle, ignores);
    }

    /**
     * Create an excel writer
     *
     * @param excelClass Excel mapped entity
     * @param response   response
     * @param ignores    Which table heads to be ignored when exporting, in the case of multiple table heads,
     *                   there are more than one child table heads under the ignored table head,
     *                   then the child table head will be ignored, if the ignored table head is from the table head
     *                   then it is ignored
     * @param fileName   Excel file name，The priority is higher than the annotation specification
     * @return ExcelWriter
     */
    public static ExcelBindWriter createWriter(String fileName, Class<?> excelClass, HttpServletResponse response, String... ignores) {
        return createWriter(fileName, excelClass, response, true, ignores);
    }

    /**
     * Create an excel writer
     *
     * @param fileName         Excel file name，The priority is higher than the annotation specification
     * @param excelClass       Excel mapped entity
     * @param response         response
     * @param ignores          Which table heads to be ignored when exporting, in the case of multiple table heads,
     *                         there are more than one child table heads under the ignored table head,
     *                         then the child table head will be ignored, if the ignored table head is from the table head
     *                         then it is ignored
     * @param initDefaultStyle Whether init default excel style
     * @return ExcelWriter
     */
    public static ExcelBindWriter createWriter(String fileName, Class<?> excelClass, HttpServletResponse response, boolean initDefaultStyle, String... ignores) {
        ParamUtils.requireNonNull(excelClass, "Excel mapping class cannot be null");
        Excel excel = excelClass.getAnnotation(Excel.class);
        ParamUtils.requireNonNull(excel, "@Excel annotation was not found on the " + excelClass);
        List<String[]> headerArr = new ArrayList<>();
        ExcelWriterContext context = ExcelWriterContext.builder()
                .excelFields(BeanUtils.getExcelFields(excelClass, ignores, headerArr))
                .headNames(headerArr)
                .excelType(excel.type())
                .fileName(StringUtils.isEmpty(fileName) ? "".equals(excel.value()) ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : excel.value() : fileName)
                .excelClass(excelClass)
                .build();
        return new ExcelBindWriter(context, excel, response, initDefaultStyle);
    }

    /**
     * Create an excel writer
     *
     * @param fileName         Excel file name
     * @param response         response
     * @param initDefaultStyle Whether init  default excel style
     * @return ExcelSimpleWriter
     */
    public static ExcelSimpleWriter createSimpleWriter(String fileName, HttpServletResponse response, boolean initDefaultStyle) {
        return createSimpleWriter(fileName, response, ExcelType.XLS, 500, initDefaultStyle);
    }

    /**
     * Create an excel writer
     *
     * @param fileName  Excel file name
     * @param response  response
     * @param excelType Excel file type
     * @return ExcelSimpleWriter
     */
    public static ExcelSimpleWriter createSimpleWriter(String fileName, HttpServletResponse response, ExcelType excelType) {
        return createSimpleWriter(fileName, response, excelType, 500, true);
    }

    /**
     * Create an excel writer
     *
     * @param fileName   Excel file name
     * @param response   response
     * @param excelType  Excel file type
     * @param windowSize Window size, which is flushed to disk when exported
     *                   if the data that has been written out exceeds the specified size
     *                   only for xlsx
     * @return ExcelSimpleWriter
     */
    public static ExcelSimpleWriter createSimpleWriter(String fileName, HttpServletResponse response, ExcelType excelType, int windowSize) {
        return createSimpleWriter(fileName, response, excelType, windowSize, true);
    }

    /**
     * Create an excel writer
     *
     * @param fileName         Excel file name
     * @param response         response
     * @param excelType        Excel file type
     * @param windowSize       Window size, which is flushed to disk when exported
     *                         if the data that has been written out exceeds the specified size
     *                         only for xlsx
     * @param initDefaultStyle Whether init  default excel style
     * @return ExcelSimpleWriter
     */
    public static ExcelSimpleWriter createSimpleWriter(String fileName, HttpServletResponse response, ExcelType excelType, int windowSize, boolean initDefaultStyle) {
        ExcelWriterContext context = ExcelWriterContext.builder()
                .fileName(StringUtils.isEmpty(fileName) ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : fileName)
                .excelType(excelType)
                .excelClass(Void.class)
                .build();
        return new ExcelSimpleWriter(context, windowSize, response, initDefaultStyle);
    }

    /**
     * Create an Excel reader
     *
     * @param file       Excel file
     * @param excelClass Excel mapped entity
     * @param ignores    Table headers to be ignored when importing, the number of table headers typically used in
     *                   Excel files does not match the number of mapped entity fields,
     *                   or some table headers are ignored when exporting templates
     * @param <R>        Entity type
     * @return ExcelReader
     */
    public static <R> ExcelReader<R> createReader(MultipartFile file, Class<R> excelClass, String... ignores) {
        try {
            return createReader(file.getInputStream(), excelClass, ignores);
        } catch (IOException e) {
            throw new ExcelInitException("Create excel reader error," + e.getMessage());
        }
    }

    /**
     * Create an Excel reader
     *
     * @param file       Excel file
     * @param excelClass Excel mapped entity
     * @param ignores    Table headers to be ignored when importing, the number of table headers typically used in
     *                   Excel files does not match the number of mapped entity fields,
     *                   or some table headers are ignored when exporting templates
     * @param <R>        Entity type
     * @return ExcelReader
     */
    public static <R> ExcelReader<R> createReader(File file, Class<R> excelClass, String... ignores) {
        try {
            return createReader(new FileInputStream(file), excelClass, ignores);
        } catch (IOException e) {
            throw new ExcelInitException("Create excel reader error," + e.getMessage());
        }
    }

    /**
     * Create an Excel reader
     *
     * @param inputStream Excel file inputStream
     * @param excelClass  Excel mapped entity
     * @param ignores     Table headers to be ignored when importing, the number of table headers typically used in
     *                    Excel files does not match the number of mapped entity fields,
     *                    or some table headers are ignored when exporting templates
     * @param <R>         Entity type
     * @return ExcelReader
     */
    public static <R> ExcelReader<R> createReader(InputStream inputStream, Class<R> excelClass, String... ignores) {
        ParamUtils.requireNonNull(excelClass, "Excel mapping class cannot be null");
        Excel excel = excelClass.getAnnotation(Excel.class);
        ParamUtils.requireNonNull(excel, "@Excel annotation was not found on the " + excelClass);
        List<Field> excelFieldList = BeanUtils.getExcelFields(excelClass, ignores, null);
        ExcelReaderContext<R> readerContext = new ExcelReaderContext<>(inputStream, excelClass, excelFieldList);
        return new ExcelReader<>(readerContext, excel);
    }
}
