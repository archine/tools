package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.metadata.ExcelWriterResolver;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ExcelUtils;
import cn.gjing.tools.excel.util.ListenerChain;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.listener.ExcelWriteListener;
import cn.gjing.tools.excel.write.style.DefaultExcelStyleWriteListener;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Excel writer
 *
 * @author Gjing
 **/
public final class ExcelWriter {
    private ExcelWriterContext context;
    private HttpServletResponse response;
    private String defaultSheetName = "sheet1";
    private ExcelWriterResolver writerResolver;

    private ExcelWriter() {

    }

    public ExcelWriter(ExcelWriterContext context, Excel excel, HttpServletResponse response, boolean initDefaultStyle) {
        this.response = response;
        this.context = context;
        this.initResolver(context, excel);
        if (initDefaultStyle) {
            this.initStyle(context);
        }
    }

    /**
     * Init resolver
     *
     * @param excel excel
     */
    private void initResolver(ExcelWriterContext context, Excel excel) {
        switch (excel.type()) {
            case XLS:
                context.setWorkbook(new HSSFWorkbook());
                this.writerResolver = new ExcelWriteXlsResolver();
                this.writerResolver.init(context);
                break;
            case XLSX:
                context.setWorkbook(new SXSSFWorkbook(excel.windowSize()));
                this.writerResolver = new ExcelWriteXlsxResolver();
                this.writerResolver.init(context);
                break;
            default:
                throw new ExcelInitException("No corresponding processor was found");
        }
    }

    /**
     * Init default style
     *
     * @param context Excel write context
     */
    private void initStyle(ExcelWriterContext context) {
        DefaultExcelStyleWriteListener defaultExcelStyle = new DefaultExcelStyleWriteListener();
        defaultExcelStyle.init(context.getWorkbook());
        ExcelUtils.addWriteListener(this.context.getWriteListenerCache(), defaultExcelStyle);
    }

    /**
     * To write
     *
     * @param data data
     * @return this
     */
    public ExcelWriter write(List<?> data) {
        return this.write(data, this.defaultSheetName, true, null);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName) {
        return this.write(data, sheetName, true, null);
    }

    /**
     * To write
     *
     * @param data     data
     * @param needHead Whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, boolean needHead) {
        return this.write(data, this.defaultSheetName, needHead, null);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @param needHead  Whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, boolean needHead) {
        return this.write(data, sheetName, needHead, null);
    }

    /**
     * To write
     *
     * @param data      data
     * @param boxValues dropdown box values
     * @return this
     */
    public ExcelWriter write(List<?> data, Map<String, String[]> boxValues) {
        return this.write(data, this.defaultSheetName, true, boxValues);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @param boxValues dropdown box values
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Map<String, String[]> boxValues) {
        return this.write(data, sheetName, true, boxValues);
    }

    /**
     * To write
     *
     * @param data      data
     * @param boxValues dropdown box values
     * @param needHead  Whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, boolean needHead, Map<String, String[]> boxValues) {
        return this.write(data, this.defaultSheetName, needHead, boxValues);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @param boxValues dropdown box values
     * @param needHead  Whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, boolean needHead, Map<String, String[]> boxValues) {
        this.createSheet(sheetName);
        this.writerResolver.writeHead(needHead, boxValues)
                .write(data);
        return this;
    }

    /**
     * Write excel big title
     * This method only fires the style listener
     *
     * @param bigTitle Big title
     * @return this
     */
    public ExcelWriter writeTitle(BigTitle bigTitle) {
        return this.writeTitle(bigTitle, this.defaultSheetName);
    }

    /**
     * Write excel big title
     * This method only fires the style listener
     *
     * @param bigTitle  Big title
     * @param sheetName Sheet name
     * @return this
     */
    public ExcelWriter writeTitle(BigTitle bigTitle, String sheetName) {
        this.createSheet(sheetName);
        if (bigTitle.getLastCols() < 1) {
            bigTitle.setLastCols(this.context.getExcelFields().size() - 1);
        }
        this.writerResolver.writeTitle(bigTitle);
        return this;
    }

    /**
     * Whether enable excel valid
     *
     * @return this
     */
    public ExcelWriter enableValid() {
        this.context.setNeedValid(true);
        return this;
    }

    /**
     * Whether enable excel valid
     *
     * @return this
     */
    public ExcelWriter closeValid() {
        this.context.setNeedValid(false);
        return this;
    }

    /**
     * Whether close multi excel head
     *
     * @return this
     */
    public ExcelWriter enableMultiHead() {
        this.context.setMultiHead(true);
        return this;
    }

    /**
     * Whether close multi excel head
     *
     * @return this
     */
    public ExcelWriter closeMultiHead() {
        this.context.setMultiHead(false);
        return this;
    }

    /**
     * Add an write listener
     *
     * @param listener Write listener
     * @return this
     */
    public ExcelWriter addListener(ExcelWriteListener listener) {
        this.context.addListener(listener);
        return this;
    }

    /**
     * Add multiple write listener
     *
     * @param listeners Write listener list
     * @return this
     */
    public ExcelWriter addListener(List<? extends ExcelWriteListener> listeners) {
        listeners.forEach(this.context::addListener);
        return this;
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
     * Reset Excel mapped entity
     *
     * @param excelClass    Excel mapped entity
     * @param ignores       The exported field is to be ignored
     * @param resetListener Whether to reset the listener
     * @return this
     */
    public ExcelWriter resetExcelClass(Class<?> excelClass, boolean resetListener, String... ignores) {
        Excel excel = excelClass.getAnnotation(Excel.class);
        ParamUtils.requireNonNull(excel, "Failed to reset Excel class, the @Excel annotation was not found on the " + excelClass);
        List<String[]> headNames = new ArrayList<>();
        this.context.setExcelFields(BeanUtils.getExcelFields(excelClass, ignores, headNames));
        this.context.setHeadNames(headNames);
        if (resetListener) {
            this.context.getWriteListenerCache().clear();
        }
        return this;
    }

    /**
     * Flush all content to excel of the cache
     */
    public void flush() {
        ListenerChain.doWorkbookFlushBefore(this.context);
        this.writerResolver.flush(this.response, this.context);
        if (this.context.getWorkbook() instanceof SXSSFWorkbook) {
            ((SXSSFWorkbook) this.context.getWorkbook()).dispose();
        }
    }

    /**
     * Create excel sheet
     *
     * @param sheetName sheet name
     */
    private void createSheet(String sheetName) {
        Sheet sheet = this.context.getWorkbook().getSheet(sheetName);
        if (sheet == null) {
            sheet = this.context.getWorkbook().createSheet(sheetName);
            context.setSheet(sheet);
            ListenerChain.doCompleteSheet(context);
        }
    }
}
