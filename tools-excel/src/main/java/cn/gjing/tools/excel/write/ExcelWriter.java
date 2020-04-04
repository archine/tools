package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.BigTitle;
import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.ExcelStyle;
import cn.gjing.tools.excel.MetaStyle;
import cn.gjing.tools.excel.exception.ExcelInitException;
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
@Getter
public class ExcelWriter {
    private String fileName;
    private MetaStyle metaStyle;
    private Workbook workbook;
    private HttpServletResponse response;
    private ExcelWriterResolver writerResolver;
    private List<Field> headFieldList;
    private Excel excel;
    private boolean needHead = true;
    private Sheet currentSheet;

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
                this.workbook = new SXSSFWorkbook(excel.maxSize());
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
        return this.write(data, "sheet1", this.metaStyle, null, null);
    }

    /**
     * Write Excel
     *
     * @param data           data
     * @param explicitValues The value in the drop-down box
     * @return this
     */
    public ExcelWriter write(List<?> data, Map<String, String[]> explicitValues) {
        return this.write(data, "sheet1", this.metaStyle, explicitValues, null);
    }

    /**
     * Write to the specified sheet
     *
     * @param data      data
     * @param sheetName sheetName
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName) {
        return this.write(data, sheetName, this.metaStyle, null, null);
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
        return this.write(data, sheetName, this.metaStyle, explicitValues, null);
    }

    /**
     * Write excel
     *
     * @param data  data
     * @param style Excel style
     * @return this
     */
    public ExcelWriter write(List<?> data, Supplier<? extends ExcelStyle> style) {
        return this.write(data, "sheet1", style, null, null);
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
        return this.write(data, "sheet1", style, explicitValues, null);
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
        return this.write(data, sheetName, style, null, null);
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
        return this.write(data, sheetName, style, null, bigTitle);
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
     * @param data     data
     * @param bigTitle Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, BigTitle bigTitle) {
        return this.write(data, "sheet1", this.metaStyle, null, bigTitle);
    }

    /**
     * Write to the specified excel
     *
     * @param data      data
     * @param sheetName Excel sheet name
     * @param bigTitle  Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, BigTitle bigTitle) {
        return this.write(data, sheetName, this.metaStyle, null, bigTitle);
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
        return this.write(data, "sheet1", style, null, bigTitle);
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
        return this.write(data, "sheet1", this.metaStyle, explicitValues, bigTitle);
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
        return this.write(data, "sheet1", style, explicitValues, bigTitle);
    }

    /**
     * Write to the specified excel
     *
     * @param data           data
     * @param sheetName      sheet name
     * @param explicitValues The value in the drop-down box
     * @param bigTitle       Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Map<String, String[]> explicitValues, BigTitle bigTitle) {
        return this.write(data, sheetName, this.metaStyle, explicitValues, bigTitle);
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
        this.needHead = true;
        this.excel = excel;
        return this;
    }

    /**
     * Output the contents of the cache
     */
    public void flush() {
        this.writerResolver.flush(this.response, this.fileName);
        if (this.workbook instanceof SXSSFWorkbook) {
            ((SXSSFWorkbook) this.workbook).dispose();
        }
    }

    /**
     * Write to the specified excel
     *
     * @param data           data
     * @param sheetName      sheet name
     * @param style          Excel style
     * @param explicitValues The value in the drop-down box
     * @param bigTitle       Big title
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Supplier<? extends ExcelStyle> style, Map<String, String[]> explicitValues, BigTitle bigTitle) {
        ExcelStyle excelStyle = style.get();
        MetaStyle metaStyle = new MetaStyle(excelStyle.setHeaderStyle(this.workbook, this.workbook.createCellStyle()), excelStyle.setBodyStyle(this.workbook, this.workbook.createCellStyle()),
                excelStyle.setTitleStyle(this.workbook, this.workbook.createCellStyle()));
        return this.write(data, sheetName, metaStyle, explicitValues, bigTitle);
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
    private ExcelWriter write(List<?> data, String sheetName, MetaStyle metaStyle, Map<String, String[]> explicitValues, BigTitle bigTitle) {
        this.initSheet(sheetName);
        boolean noContent = data == null || data.isEmpty();
        this.writerResolver.writeTitle(this.headFieldList.size(), bigTitle, metaStyle, this.currentSheet)
                .writeHead(noContent, this.headFieldList, this.currentSheet, this.needHead, metaStyle, explicitValues, excel)
                .write(data, this.currentSheet, this.headFieldList, this.metaStyle, false);
        this.needHead = false;
        return this;
    }

    /**
     * Init excel sheet
     *
     * @param sheetName sheet name
     */
    private void initSheet(String sheetName) {
        Sheet sheet = this.workbook.getSheet(sheetName);
        if (sheet == null) {
            this.needHead = true;
            sheet = this.workbook.createSheet(sheetName);
            if (this.excel.lock()) {
                sheet.protectSheet(this.excel.secret());
            }
        }
        this.currentSheet = sheet;
    }
}
