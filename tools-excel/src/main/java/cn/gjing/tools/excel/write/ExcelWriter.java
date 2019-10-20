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

    /**
     * excel文件名
     */
    private String fileName;

    /**
     * excel样式
     */
    private MetaStyle metaStyle;

    /**
     * 工作簿
     */
    private Workbook workbook;

    /**
     * response
     */
    private HttpServletResponse response;

    /**
     * 写Excel的解析器
     */
    private ExcelWriterResolver writerResolver;

    /**
     * excel列表头字段
     */
    private List<Field> headFieldList;

    /**
     * 大标题
     */
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
     * 初始化解析器
     *
     * @param excel excel
     */
    private void initResolver(Excel excel) {
        switch (excel.type()) {
            case XLS:
                this.workbook = new HSSFWorkbook();
                this.writerResolver = new ExcelWriteXLSResolver();
                break;
            case XLSX:
                this.workbook = new SXSSFWorkbook();
                this.writerResolver = new ExcelWriteXLSXResolver();
                break;
            default:
        }
    }

    /**
     * 初始化样式
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
     * 写excel
     *
     * @param data 要导出的数据
     * @return this
     */
    public ExcelWriter write(List<?> data) {
        this.writerResolver.write(data, this.workbook, "sheet1", this.headFieldList, this.metaStyle, this.bigTitle);
        this.bigTitle = null;
        return this;
    }

    /**
     * 写入指定的sheet中
     *
     * @param data      要导出的数据
     * @param sheetName sheetName
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName) {
        this.writerResolver.write(data, this.workbook, sheetName, this.headFieldList, this.metaStyle, this.bigTitle);
        this.bigTitle = null;
        return this;
    }

    /**
     * 重置处理器, <b>该操作要在其他操作之前进行, 否则之前的操作会无效</b>
     * @param excelWriteResolver 读处理器
     * @return this
     */
    public ExcelWriter resetResolver(Supplier<? extends ExcelWriterResolver> excelWriteResolver) {
        this.writerResolver = excelWriteResolver.get();
        return this;
    }

    /**
     * 重置模板
     *
     * @param excelClass 导出的这个Excel对应的Class
     * @param ignores    忽略导出的字段
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
     * 添加大标题
     *
     * @param bigTitle 大标题提供者
     * @return this
     */
    public ExcelWriter bigTitle(Supplier<? extends BigTitle> bigTitle) {
        this.bigTitle = bigTitle.get();
        return this;
    }

    /**
     * 输出缓存中的内容
     */
    public void flush() {
        this.writerResolver.flush(this.response, this.fileName);
    }

}
