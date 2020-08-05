package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.metadata.ExcelWriterResolver;
import cn.gjing.tools.excel.util.ListenerChain;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Gjing
 **/
public abstract class ExcelBaseWriter {
    protected ExcelWriterContext context;
    protected HttpServletResponse response;
    protected ExcelWriterResolver writerResolver;
    protected final String defaultSheetName = "Sheet1";

    protected ExcelBaseWriter(ExcelWriterContext context, int windowSize, HttpServletResponse response, boolean initDefaultStyle) {
        this.response = response;
        this.context = context;
        this.chooseResolver(context, windowSize);
        if (initDefaultStyle) {
            this.initStyle();
        }
    }

    /**
     * Init default style listener
     */
    protected abstract void initStyle();

    /**
     * Choose resolver
     *
     * @param context Excel write context
     */
    protected void chooseResolver(ExcelWriterContext context, int windowSize) {
        switch (this.context.getExcelType()) {
            case XLS:
                context.setWorkbook(new HSSFWorkbook());
                this.writerResolver = new ExcelWriteXlsResolver();
                this.writerResolver.init(context);
                break;
            case XLSX:
                context.setWorkbook(new SXSSFWorkbook(windowSize));
                this.writerResolver = new ExcelWriteXlsxResolver();
                this.writerResolver.init(context);
                break;
            default:
                throw new ExcelResolverException("No corresponding resolver was found");
        }
    }

    /**
     * Flush all content to excel of the cache
     */
    public void flush() {
        ListenerChain.doWorkbookFlushBefore(this.context);
        if (this.context.isIdentifier()) {
            this.context.getWorkbook().createSheet("identificationSheet")
                    .createRow(0)
                    .createCell(0)
                    .setCellValue(this.context.getExcelClass().getSimpleName());
            this.context.getWorkbook().setSheetHidden(this.context.getWorkbook().getSheetIndex("identificationSheet"), true);
        }
        this.writerResolver.flush(this.response, this.context, this.context.getExcelType());
        if (this.context.getWorkbook() instanceof SXSSFWorkbook) {
            ((SXSSFWorkbook) this.context.getWorkbook()).dispose();
        }
    }

    /**
     * Create excel sheet
     *
     * @param sheetName sheet name
     */
    public void createSheet(String sheetName) {
        Sheet sheet = this.context.getWorkbook().getSheet(sheetName);
        if (sheet == null) {
            sheet = this.context.getWorkbook().createSheet(sheetName);
            context.setSheet(sheet);
            ListenerChain.doCompleteSheet(context);
        }
    }
}
