package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.metadata.ExecType;
import cn.gjing.tools.excel.util.ListenerChain;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.resolver.core.ExcelWriteXlsResolver;
import cn.gjing.tools.excel.write.resolver.core.ExcelWriteXlsxResolver;
import cn.gjing.tools.excel.write.resolver.core.ExcelWriterResolver;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Excel base writer
 *
 * @author Gjing
 **/
public abstract class ExcelBaseWriter {
    protected ExcelWriterContext context;
    protected HttpServletResponse response;
    protected ExcelWriterResolver writerResolver;
    protected final String defaultSheetName = "Sheet1";
    protected boolean nullIsTemp = true;

    protected ExcelBaseWriter(ExcelWriterContext context, int windowSize, HttpServletResponse response, boolean initDefaultStyle, ExecType type) {
        this.response = response;
        this.context = context;
        this.chooseResolver(context, windowSize, type);
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
     * @param context    Excel write context
     * @param windowSize Window size, which is flushed to disk when exported
     *                   if the data that has been written out exceeds the specified size
     * @param execType   Write executor type
     */
    protected void chooseResolver(ExcelWriterContext context, int windowSize, ExecType execType) {
        switch (this.context.getExcelType()) {
            case XLS:
                context.setWorkbook(new HSSFWorkbook());
                this.writerResolver = new ExcelWriteXlsResolver(context, execType);
                break;
            case XLSX:
                context.setWorkbook(new SXSSFWorkbook(windowSize));
                this.writerResolver = new ExcelWriteXlsxResolver(context, execType);
                break;
            default:
        }
    }

    /**
     * Flush all content to excel of the cache
     */
    public void flush() {
        this.processBind();
        if (ListenerChain.doWorkbookFlushBefore(this.context.getListenerCache(), this.context.getWorkbook())) {
            this.writerResolver.flush(this.response, this.context);
            if (this.context.getWorkbook() instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) this.context.getWorkbook()).dispose();
            }
        }
    }

    /**
     * Flush all content to excel of the cache to local
     *
     * @param path Absolute path to the directory where the file is stored
     */
    public void flushToLocal(String path) {
        this.processBind();
        if (ListenerChain.doWorkbookFlushBefore(this.context.getListenerCache(), this.context.getWorkbook())) {
            this.writerResolver.flushToLocal(path, this.context);
            if (this.context.getWorkbook() instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) this.context.getWorkbook()).dispose();
            }
        }
    }

    /**
     * Create Excel sheet
     *
     * @param sheetName sheet name
     */
    public void createSheet(String sheetName) {
        Sheet sheet = this.context.getWorkbook().getSheet(sheetName);
        if (sheet != null) {
            this.context.setSheet(sheet);
            return;
        }
        sheet = this.context.getWorkbook().createSheet(sheetName);
        this.context.setSheet(sheet);
        ListenerChain.doCompleteSheet(this.context.getListenerCache(), sheet);
    }

    private void processBind() {
        if (!this.context.isBind()) {
            return;
        }
        String unqSheetName = "excelUnqSheet";
        Sheet sheet = this.context.getWorkbook().createSheet(unqSheetName);
        sheet.protectSheet(UUID.randomUUID().toString().replaceAll("-", ""));
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue(ParamUtils.encodeMd5(this.context.getUniqueKey()));
        this.context.getWorkbook().setSheetHidden(this.context.getWorkbook().getSheetIndex(sheet), true);
    }
}
