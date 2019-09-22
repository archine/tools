package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.BigTitle;
import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.ExcelStyle;
import cn.gjing.tools.excel.resolver.ExcelWriterResolver;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;

/**
 * Excel writer
 *
 * @author Gjing
 **/
@SuppressWarnings("unused")
public class ExcelWriter {

    /**
     * excel文件名
     */
    private String fileName;

    /**
     * excel注解
     */
    private Excel excel;

    /**
     * excel列表头
     */
    private List<String> header;

    /**
     * response
     */
    private HttpServletResponse response;

    /**
     * Excel需要导出的实体集合
     */
    private List<?> excelClassList;

    /**
     * 写Excel的解析器
     */
    private ExcelWriterResolver excelWriterResolver;

    /**
     * excel列表头字段
     */
    private List<Field> fieldList;

    private ExcelWriter() {

    }

    public ExcelWriter(String fileName, Excel excel, List<String> header, HttpServletResponse response, List<?> excelClassList, List<Field> excelFieldList) {
        this.fileName = fileName;
        this.header = header;
        this.response = response;
        this.excelClassList = excelClassList;
        this.excel = excel;
        this.fieldList = excelFieldList;
        this.chooseResolver();
    }

    /**
     * 选择默认的解析器
     */
    private void chooseResolver() {
        switch (excel.type()) {
            case XLS:
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
                this.excelWriterResolver = new ExcelWriteXLSResolver().builder(hssfWorkbook, response, header, fieldList, fileName);
                break;
            case XLSX:
                SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook();
                this.excelWriterResolver = new ExcelWriteXLSXResolver().builder(sxssfWorkbook, response, header, fieldList, fileName);
                break;
            default:
        }
    }

    /**
     * 写excel
     */
    public void write() {
        this.excelWriterResolver.write(excelClassList);
    }

    /**
     * 使用用户定义的解析器
     *
     * @param writeResolver customWriteResolver
     * @return ExcelWriter
     */
    public ExcelWriter changeResolver(Supplier<? extends ExcelWriterResolver> writeResolver, Workbook workbook) {
        this.excelWriterResolver = writeResolver.get().builder(workbook, this.response, this.header, this.fieldList, this.fileName);
        return this;
    }

    /**
     * 添加大标题
     *
     * @param supplier 大标题提供者
     * @return ExcelWriter
     */
    public ExcelWriter bigTitle(Supplier<? extends BigTitle> supplier) {
        this.excelWriterResolver.setBigTitle(supplier.get());
        return this;
    }

    /**
     * 设置列表头样式
     *
     * @param headerStyle headerStyle
     * @return ExcelWriter
     */
    public ExcelWriter setHeaderStyle(Supplier<? extends ExcelStyle> headerStyle) {
        this.excelWriterResolver.setHeaderStyle(headerStyle);
        return this;
    }

    /**
     * 设置正文样式
     *
     * @param contentStyle headerStyle
     * @return ExcelWriter
     */
    public ExcelWriter setContentStyle(Supplier<? extends ExcelStyle> contentStyle) {
        this.excelWriterResolver.setContentStyle(contentStyle);
        return this;
    }

    /**
     * 设置大标题样式
     *
     * @param bigTitleStyle headerStyle
     * @return ExcelWriter
     */
    public ExcelWriter setBigTitleStyle(Supplier<? extends ExcelStyle> bigTitleStyle) {
        this.excelWriterResolver.setBigTitleStyle(bigTitleStyle);
        return this;
    }
}
