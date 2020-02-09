package cn.gjing.tools.excel.write;


import cn.gjing.tools.excel.*;
import cn.gjing.tools.excel.resolver.ExcelWriterResolver;
import cn.gjing.tools.excel.util.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Excel writer
 *
 * @author Gjing
 **/
public class ExcelWriter {

    private String fileName;
    private MetaStyle metaStyle;
    private Workbook workbook;
    private HttpServletResponse response;
    private ExcelWriterResolver writerResolver;
    private List<Field> headFieldList;
    private boolean changed = true;
    private String defaultSheet = "sheet1";

    private ExcelWriter() {

    }

    public ExcelWriter(String fileName, Excel excel, HttpServletResponse response, List<Field> headFieldList) {
        this.fileName = fileName;
        this.response = response;
        this.headFieldList = headFieldList;
        this.initResolver(excel);
        this.initStyle(excel, this.workbook);
    }

    /**
     * Init resolver
     *
     * @param excel excel
     */
    private void initResolver(Excel excel) {
        switch (excel.type()) {
            case XLS:
                this.workbook = new HSSFWorkbook();
                try (final ExcelWriteXLSResolver xlsResolver = new ExcelWriteXLSResolver()) {
                    this.writerResolver = xlsResolver;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case XLSX:
                this.workbook = new SXSSFWorkbook();
                try (final ExcelWriteXLSXResolver xlsxResolver = new ExcelWriteXLSXResolver()) {
                    this.writerResolver = xlsxResolver;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
        }
    }

    /**
     * Init style
     *
     * @param excel excel
     */
    private void initStyle(Excel excel, Workbook workbook) {
        try {
            ExcelStyle excelStyle = excel.style().newInstance();
            this.metaStyle = new MetaStyle(excelStyle.setHeaderStyle(workbook.createCellStyle()), excelStyle.setBodyStyle(workbook.createCellStyle()),
                    excelStyle.setTitleStyle(workbook.createCellStyle()));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write Excel
     *
     * @param data data
     * @return this
     */
    public ExcelWriter write(List<?> data) {
        return this.write(data, this.defaultSheet, this.metaStyle, new HashMap<>(16),null);
    }

    /**
     * Write Excel
     *
     * @param data           data
     * @param explicitValues The value in the drop-down box
     * @return this
     */
    public ExcelWriter write(List<?> data, Map<String, String[]> explicitValues) {
        return this.write(data, this.defaultSheet, this.metaStyle, explicitValues,null);
    }

    /**
     * Write to the specified sheet
     *
     * @param data      data
     * @param sheetName sheetName
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName) {
        return this.write(data, sheetName, this.metaStyle, new HashMap<>(16),null);
    }

    /**
     * Write to the specified sheet
     *
     * @param data           data
     * @param sheetName      sheetName
     * @param explicitValues The value in the drop-down box
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Map<String, String[]> explicitValues) {
        return this.write(data, sheetName, this.metaStyle, explicitValues,null);
    }

    /**
     * Write excel
     *
     * @param data  data
     * @param style Excel style
     * @return this
     */
    public ExcelWriter write(List<?> data, Supplier<? extends ExcelStyle> style) {
        return this.write(data, this.defaultSheet, style, new HashMap<>(16), null);
    }

    /**
     * Write excel
     *
     * @param data           data
     * @param style          Excel style
     * @param explicitValues The value in the drop-down box
     * @return this
     */
    public ExcelWriter write(List<?> data, Supplier<? extends ExcelStyle> style, Map<String, String[]> explicitValues) {
        return this.write(data, this.defaultSheet, style, explicitValues, null);
    }

    /**
     * Write to the specified excel
     *
     * @param data      data
     * @param sheetName Excel sheet name
     * @param style     Excel style
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Supplier<? extends ExcelStyle> style) {
        return this.write(data, sheetName, style, new HashMap<>(16), null);
    }

    /**
     * Write to the specified excel
     *
     * @param data      data
     * @param sheetName Excel sheet name
     * @param style     Excel style
     * @param bigTitle Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Supplier<? extends ExcelStyle> style,BigTitle bigTitle) {
        return this.write(data, sheetName, style, new HashMap<>(16), bigTitle);
    }

    /**
     * Write to the specified excel
     *
     * @param data           data
     * @param sheetName      Excel sheet name
     * @param style          Excel style
     * @param explicitValues The value in the drop-down box
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Supplier<? extends ExcelStyle> style, Map<String, String[]> explicitValues) {
        return this.write(data, sheetName, style, explicitValues, null);
    }

    /**
     * Write excel
     *
     * @param data           data
     * @param bigTitle Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, BigTitle bigTitle) {
        return this.write(data, this.defaultSheet, this.metaStyle, new HashMap<>(16), bigTitle);
    }

    /**
     * Write to the specified excel
     *
     * @param data           data
     * @param sheetName      Excel sheet name
     * @param bigTitle Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, BigTitle bigTitle) {
        return this.write(data, sheetName, this.metaStyle, new HashMap<>(16), bigTitle);
    }

    /**
     * Write excel
     *
     * @param data           data
     * @param style          Excel style
     * @param bigTitle Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, Supplier<? extends ExcelStyle> style, BigTitle bigTitle) {
        return this.write(data, this.defaultSheet, style, new HashMap<>(16), bigTitle);
    }

    /**
     * Write excel
     *
     * @param data           data
     * @param explicitValues The value in the drop-down box
     * @param bigTitle Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, Map<String, String[]> explicitValues, BigTitle bigTitle) {
        return this.write(data, this.defaultSheet, this.metaStyle, explicitValues, bigTitle);
    }

    /**
     * Write excel
     *
     * @param data           data
     * @param style          Excel style
     * @param explicitValues The value in the drop-down box
     * @param bigTitle Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, Supplier<? extends ExcelStyle> style, Map<String, String[]> explicitValues, BigTitle bigTitle) {
        return this.write(data, this.defaultSheet, style, explicitValues, bigTitle);
    }

    /**
     * Write to the specified excel
     *
     * @param data           data
     * @param sheetName      Excel sheet name
     * @param explicitValues The value in the drop-down box
     * @param bigTitle Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Map<String, String[]> explicitValues, BigTitle bigTitle) {
        return this.write(data, sheetName, this.metaStyle, explicitValues, bigTitle);
    }

    /**
     * Write to the specified excel
     *
     * @param data           data
     * @param sheetName      Excel sheet name
     * @param style          Excel style
     * @param explicitValues The value in the drop-down box
     * @param bigTitle Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Supplier<? extends ExcelStyle> style, Map<String, String[]> explicitValues, BigTitle bigTitle) {
        ExcelStyle excelStyle = style.get();
        MetaStyle metaStyle = new MetaStyle(excelStyle.setHeaderStyle(this.workbook.createCellStyle()), excelStyle.setBodyStyle(this.workbook.createCellStyle()),
                excelStyle.setTitleStyle(this.workbook.createCellStyle()));
        return this.write(data, sheetName, metaStyle, explicitValues, bigTitle);
    }

    /**
     * Reset the processor before any other operation
     *
     * @param excelWriteResolver Excel write Resolver
     * @return this
     */
    public ExcelWriter resetResolver(Supplier<? extends ExcelWriterResolver> excelWriteResolver) {
        this.writerResolver = excelWriteResolver.get();
        return this;
    }

    /**
     * Reset Excel mapped entity
     *
     * @param excelClass Excel mapped entity
     * @param ignores    The exported field is to be ignored
     * @return this
     */
    public ExcelWriter resetExcelClass(Class<?> excelClass, String... ignores) {
        Excel excel = excelClass.getAnnotation(Excel.class);
        Objects.requireNonNull(excel, "@Excel was not found on the excelClass");
        try {
            ExcelStyle excelStyle = excel.style().newInstance();
            this.metaStyle = new MetaStyle(excelStyle.setHeaderStyle(this.workbook.createCellStyle()), excelStyle.setBodyStyle(this.workbook.createCellStyle()),
                    excelStyle.setTitleStyle(this.workbook.createCellStyle()));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        this.headFieldList = BeanUtils.getFields(excelClass, ignores);
        this.changed = true;
        return this;
    }

    /**
     * Output the contents of the cache
     */
    public void flush() {
        this.writerResolver.flush(this.response, this.fileName);
    }

    /**
     * To write
     *
     * @param data           data
     * @param sheetName      Excel sheet name
     * @param metaStyle      Excel style
     * @param explicitValues The value in the drop-down box
     * @return this
     */
    private ExcelWriter write(List<?> data, String sheetName, MetaStyle metaStyle, Map<String, String[]> explicitValues, BigTitle bigTitle) {
        this.writerResolver.write(data, this.workbook, sheetName, this.headFieldList, new MetaObject(bigTitle, metaStyle, explicitValues), this.changed);
        this.changed = false;
        return this;
    }
}
