package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.BigTitle;
import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.ExcelStyle;
import cn.gjing.tools.excel.MetaStyle;
import cn.gjing.tools.excel.resolver.ExcelWriterResolver;
import cn.gjing.tools.excel.util.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.List;
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
    private BigTitle bigTitle;

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
        this.writerResolver.write(data, this.workbook, "sheet1", this.headFieldList, this.metaStyle, this.bigTitle);
        this.bigTitle = null;
        return this;
    }

    /**
     * Write to the specified sheet
     *
     * @param data      data
     * @param sheetName sheetName
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName) {
        this.writerResolver.write(data, this.workbook, sheetName, this.headFieldList, this.metaStyle, this.bigTitle);
        this.bigTitle = null;
        return this;
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
        return this;
    }

    /**
     * Set big title
     *
     * @param bigTitle Big title supplier
     * @return this
     */
    public ExcelWriter bigTitle(Supplier<? extends BigTitle> bigTitle) {
        this.bigTitle = bigTitle.get();
        return this;
    }

    /**
     * Output the contents of the cache
     */
    public void flush() {
        this.writerResolver.flush(this.response, this.fileName);
    }

}
