package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.BigTitle;
import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.ExcelStyle;
import cn.gjing.tools.excel.MetaStyle;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.listen.CustomWrite;
import cn.gjing.tools.excel.resolver.ExcelWriterResolver;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import lombok.Getter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Excel writer
 *
 * @author Gjing
 **/
public class ExcelWriter {
    @Getter
    private String fileName;
    @Getter
    private MetaStyle metaStyle;
    @Getter
    private Workbook workbook;
    @Getter
    private HttpServletResponse response;
    @Getter
    private List<Field> headFieldList;
    @Getter
    private Excel excel;
    @Getter
    private String defaultSheetName = "sheet1";
    private ExcelWriterResolver writerResolver;
    private boolean needInit;

    private ExcelWriter() {

    }

    public ExcelWriter(String fileName, Excel excel, HttpServletResponse response, List<Field> headFieldList) {
        this.fileName = fileName;
        this.response = response;
        this.headFieldList = headFieldList;
        this.excel = excel;
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
                this.writerResolver = new ExcelWriteXlsResolver((HSSFWorkbook) workbook);
                break;
            case XLSX:
                this.workbook = new SXSSFWorkbook(excel.windowSize());
                this.writerResolver = new ExcelWriteXlsxResolver((SXSSFWorkbook) workbook);
                break;
            default:
                throw new ExcelInitException("No corresponding processor was found");
        }
    }

    /**
     * Init global style
     *
     * @param excel excel
     */
    private void initStyle(Excel excel, Workbook workbook) {
        try {
            ExcelStyle excelStyle = excel.style().newInstance();
            this.metaStyle = new MetaStyle(excelStyle.setHeaderStyle(this.workbook, workbook.createCellStyle()), excelStyle.setBodyStyle(this.workbook, workbook.createCellStyle()),
                    excelStyle.setTitleStyle(this.workbook, workbook.createCellStyle()));
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ExcelInitException("Init excel style failure " + e.getMessage());
        }
    }

    /**
     * Write Excel
     *
     * @param data data
     * @return this
     */
    public ExcelWriter write(List<?> data) {
        return this.write(data, this.defaultSheetName, this.metaStyle, null, null, true);
    }

    /**
     * Write Excel
     *
     * @param data     data
     * @param needHead whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, boolean needHead) {
        return this.write(data, this.defaultSheetName, this.metaStyle, null, null, needHead);
    }

    /**
     * Write Excel
     *
     * @param data           data
     * @param explicitValues The value in the drop-down box
     * @return this
     */
    public ExcelWriter write(List<?> data, Map<String, String[]> explicitValues) {
        return this.write(data, this.defaultSheetName, this.metaStyle, explicitValues, null, true);
    }

    /**
     * Write Excel
     *
     * @param data           data
     * @param explicitValues The value in the drop-down box
     * @param needHead       whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, Map<String, String[]> explicitValues, boolean needHead) {
        return this.write(data, this.defaultSheetName, this.metaStyle, explicitValues, null, needHead);
    }

    /**
     * Write to the specified sheet
     *
     * @param data      data
     * @param sheetName sheetName
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName) {
        return this.write(data, sheetName, this.metaStyle, null, null, true);
    }

    /**
     * Write to the specified sheet
     *
     * @param data      data
     * @param sheetName sheetName
     * @param needHead  whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, boolean needHead) {
        return this.write(data, sheetName, this.metaStyle, null, null, needHead);
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
        return this.write(data, sheetName, this.metaStyle, explicitValues, null, true);
    }

    /**
     * Write to the specified sheet
     *
     * @param data           data
     * @param sheetName      sheetName
     * @param explicitValues The value in the drop-down box
     * @param needHead       whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Map<String, String[]> explicitValues, boolean needHead) {
        return this.write(data, sheetName, this.metaStyle, explicitValues, null, needHead);
    }

    /**
     * Write excel
     *
     * @param data  data
     * @param style Excel style
     * @return this
     */
    public ExcelWriter write(List<?> data, Supplier<? extends ExcelStyle> style) {
        return this.write(data, this.defaultSheetName, style, null, null, true);
    }

    /**
     * Write excel
     *
     * @param data     data
     * @param style    Excel style
     * @param needHead whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, Supplier<? extends ExcelStyle> style, boolean needHead) {
        return this.write(data, this.defaultSheetName, style, null, null, needHead);
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
        return this.write(data, this.defaultSheetName, style, explicitValues, null, true);
    }

    /**
     * Write excel
     *
     * @param data           data
     * @param style          Excel style
     * @param explicitValues The value in the drop-down box
     * @param needHead       whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, Supplier<? extends ExcelStyle> style, Map<String, String[]> explicitValues, boolean needHead) {
        return this.write(data, this.defaultSheetName, style, explicitValues, null, needHead);
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
        return this.write(data, sheetName, style, null, null, true);
    }

    /**
     * Write to the specified excel
     *
     * @param data      data
     * @param sheetName Excel sheet name
     * @param style     Excel style
     * @param needHead  whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Supplier<? extends ExcelStyle> style, boolean needHead) {
        return this.write(data, sheetName, style, null, null, needHead);
    }

    /**
     * Write to the specified excel
     *
     * @param data      data
     * @param sheetName Excel sheet name
     * @param style     Excel style
     * @param bigTitle  Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Supplier<? extends ExcelStyle> style, BigTitle bigTitle) {
        return this.write(data, sheetName, style, null, bigTitle, true);
    }

    /**
     * Write to the specified excel
     *
     * @param data      data
     * @param sheetName Excel sheet name
     * @param style     Excel style
     * @param bigTitle  Big title
     * @param needHead  whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Supplier<? extends ExcelStyle> style, BigTitle bigTitle, boolean needHead) {
        return this.write(data, sheetName, style, null, bigTitle, needHead);
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
        return this.write(data, sheetName, style, explicitValues, null, true);
    }

    /**
     * Write to the specified excel
     *
     * @param data           data
     * @param sheetName      Excel sheet name
     * @param style          Excel style
     * @param explicitValues The value in the drop-down box
     * @param needHead       whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Supplier<? extends ExcelStyle> style, Map<String, String[]> explicitValues, boolean needHead) {
        return this.write(data, sheetName, style, explicitValues, null, needHead);
    }

    /**
     * Write excel
     *
     * @param data     data
     * @param bigTitle Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, BigTitle bigTitle) {
        return this.write(data, this.defaultSheetName, this.metaStyle, null, bigTitle, true);
    }

    /**
     * Write excel
     *
     * @param data     data
     * @param bigTitle Big title
     * @param needHead whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, BigTitle bigTitle, boolean needHead) {
        return this.write(data, this.defaultSheetName, this.metaStyle, null, bigTitle, needHead);
    }

    /**
     * Write to the specified sheet
     *
     * @param data      data
     * @param sheetName Excel sheet name
     * @param bigTitle  Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, BigTitle bigTitle) {
        return this.write(data, sheetName, this.metaStyle, null, bigTitle, true);
    }

    /**
     * Write to the specified sheet
     *
     * @param data      data
     * @param sheetName Excel sheet name
     * @param bigTitle  Big title
     * @param needHead  whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, BigTitle bigTitle, boolean needHead) {
        return this.write(data, sheetName, this.metaStyle, null, bigTitle, needHead);
    }

    /**
     * Write excel
     *
     * @param data     data
     * @param style    Excel style
     * @param bigTitle Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, Supplier<? extends ExcelStyle> style, BigTitle bigTitle) {
        return this.write(data, this.defaultSheetName, style, null, bigTitle, true);
    }

    /**
     * Write excel
     *
     * @param data     data
     * @param style    Excel style
     * @param bigTitle Big title
     * @param needHead whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, Supplier<? extends ExcelStyle> style, BigTitle bigTitle, boolean needHead) {
        return this.write(data, this.defaultSheetName, style, null, bigTitle, needHead);
    }

    /**
     * Write excel
     *
     * @param data           data
     * @param explicitValues The value in the drop-down box
     * @param bigTitle       Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, Map<String, String[]> explicitValues, BigTitle bigTitle) {
        return this.write(data, this.defaultSheetName, this.metaStyle, explicitValues, bigTitle, true);
    }

    /**
     * Write excel
     *
     * @param data           data
     * @param explicitValues The value in the drop-down box
     * @param bigTitle       Big title
     * @param needHead       whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, Map<String, String[]> explicitValues, BigTitle bigTitle, boolean needHead) {
        return this.write(data, this.defaultSheetName, this.metaStyle, explicitValues, bigTitle, needHead);
    }

    /**
     * Write excel
     *
     * @param data           data
     * @param style          Excel style
     * @param explicitValues The value in the drop-down box
     * @param bigTitle       Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, Supplier<? extends ExcelStyle> style, Map<String, String[]> explicitValues, BigTitle bigTitle) {
        return this.write(data, this.defaultSheetName, style, explicitValues, bigTitle, true);
    }

    /**
     * Write excel
     *
     * @param data           data
     * @param style          Excel style
     * @param explicitValues The value in the drop-down box
     * @param bigTitle       Big title
     * @param needHead       whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, Supplier<? extends ExcelStyle> style, Map<String, String[]> explicitValues, BigTitle bigTitle, boolean needHead) {
        return this.write(data, this.defaultSheetName, style, explicitValues, bigTitle, needHead);
    }

    /**
     * Write to the specified sheet
     *
     * @param data           data
     * @param sheetName      sheet name
     * @param explicitValues The value in the drop-down box
     * @param bigTitle       Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Map<String, String[]> explicitValues, BigTitle bigTitle) {
        return this.write(data, sheetName, this.metaStyle, explicitValues, bigTitle, true);
    }

    /**
     * Write to the specified sheet
     *
     * @param data           data
     * @param sheetName      sheet name
     * @param explicitValues The value in the drop-down box
     * @param bigTitle       Big title
     * @param needHead       whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Map<String, String[]> explicitValues, BigTitle bigTitle, boolean needHead) {
        return this.write(data, sheetName, this.metaStyle, explicitValues, bigTitle, needHead);
    }

    /**
     * Reset the processor before write operation
     *
     * @param excelWriteResolver Excel write Resolver
     * @return this
     */
    public ExcelWriter resetResolver(Supplier<? extends ExcelWriterResolver> excelWriteResolver) {
        this.writerResolver = excelWriteResolver.get();
        return this;
    }

    /**
     * Write to the specified sheet
     *
     * @param data           data
     * @param sheetName      sheet name
     * @param style          Excel style
     * @param explicitValues The value in the drop-down box
     * @param bigTitle       Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Supplier<? extends ExcelStyle> style, Map<String, String[]> explicitValues, BigTitle bigTitle, boolean needHead) {
        ExcelStyle excelStyle = style.get();
        MetaStyle metaStyle = new MetaStyle(excelStyle.setHeaderStyle(this.workbook, this.workbook.createCellStyle()), excelStyle.setBodyStyle(this.workbook, this.workbook.createCellStyle()),
                excelStyle.setTitleStyle(this.workbook, this.workbook.createCellStyle()));
        return this.write(data, sheetName, metaStyle, explicitValues, bigTitle, needHead);
    }

    /**
     * User defined export
     *
     * @param customWrite export logic
     * @return this
     */
    public ExcelWriter customWrite(CustomWrite customWrite) {
        this.writerResolver.customWrite(customWrite);
        return this;
    }

    /**
     * To write
     *
     * @param data           data
     * @param sheetName      sheet name
     * @param metaStyle      Excel style
     * @param explicitValues The value in the drop-down box
     * @param bigTitle       Big title
     * @return this
     */
    private ExcelWriter write(List<?> data, String sheetName, MetaStyle metaStyle, Map<String, String[]> explicitValues, BigTitle bigTitle, boolean needHead) {
        Sheet sheet = this.initSheet(sheetName);
        if (bigTitle != null && bigTitle.getCols() < 1) {
            bigTitle.setCols(this.headFieldList.size());
        }
        if (needHead) {
            this.needInit = false;
        }
        this.writerResolver.writeTitle(bigTitle, metaStyle, sheet)
                .writeHead(data == null, this.headFieldList, sheet, needHead, metaStyle, explicitValues, excel)
                .write(data, sheet, this.headFieldList, metaStyle, this.needInit);
        this.needInit = false;
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
        ParamUtils.requireNonNull(excel, "Failed to reset Excel class, the @Excel annotation was not found on the " + excelClass);
        ExcelStyle excelStyle;
        try {
            excelStyle = excel.style().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ExcelInitException("Failed to reset Excel class, init excel style failure, " + e.getMessage());
        }
        this.metaStyle = new MetaStyle(excelStyle.setHeaderStyle(this.workbook, this.workbook.createCellStyle()), excelStyle.setBodyStyle(this.workbook, this.workbook.createCellStyle()),
                excelStyle.setTitleStyle(this.workbook, this.workbook.createCellStyle()));
        this.headFieldList = BeanUtils.getExcelFields(excelClass, ignores);
        this.excel = excel;
        this.needInit = true;
        return this;
    }

    /**
     * Output the contents to excel of the cache
     */
    public void flush() {
        this.writerResolver.flush(this.response, this.fileName);
        if (this.workbook instanceof SXSSFWorkbook) {
            ((SXSSFWorkbook) this.workbook).dispose();
        }
    }

    /**
     * Init excel sheet
     *
     * @param sheetName sheet name
     */
    private Sheet initSheet(String sheetName) {
        Sheet sheet = this.workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = this.workbook.createSheet(sheetName);
            if (this.excel.lock()) {
                sheet.protectSheet(this.excel.secret());
            }
            this.needInit = true;
        }
        return sheet;
    }
}
