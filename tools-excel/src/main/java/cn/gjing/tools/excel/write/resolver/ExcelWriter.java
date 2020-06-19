package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.metadata.ExcelWriterResolver;
import cn.gjing.tools.excel.util.BeanUtils;
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
    private final String defaultSheetName = "Sheet1";
    private ExcelWriterResolver writerResolver;

    private ExcelWriter() {

    }

    public ExcelWriter(ExcelWriterContext context, Excel excel, HttpServletResponse response, boolean initDefaultStyle) {
        this.response = response;
        this.context = context;
        this.chooseResolver(context, excel);
        if (initDefaultStyle) {
            this.initStyle();
        }
    }

    /**
     * Choose resolver
     *
     * @param excel excel
     */
    private void chooseResolver(ExcelWriterContext context, Excel excel) {
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
                throw new ExcelInitException("No corresponding resolver was found");
        }
    }

    /**
     * Init default style listener
     */
    private void initStyle() {
        DefaultExcelStyleWriteListener defaultExcelStyle = new DefaultExcelStyleWriteListener();
        this.context.addListener(defaultExcelStyle);
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
     * Write an Excel header that does not trigger a row callback or cell callback
     *
     * @param bigTitle Big title
     * @return this
     */
    public ExcelWriter writeTitle(BigTitle bigTitle) {
        return this.writeTitle(bigTitle, this.defaultSheetName);
    }

    /**
     * Write an Excel header that does not trigger a row listener or cell listener
     *
     * @param bigTitle  Big title
     * @param sheetName Sheet name
     * @return this
     */
    public ExcelWriter writeTitle(BigTitle bigTitle, String sheetName) {
        if (bigTitle != null) {
            this.createSheet(sheetName);
            if (bigTitle.getLastCols() < 1) {
                bigTitle.setLastCols(this.context.getExcelFields().size() - 1);
            }
            this.writerResolver.writeTitle(bigTitle);
        }
        return this;
    }

    /**
     * Whether enable excel valid
     *
     * @return this
     */
    @Deprecated
    public ExcelWriter enableValid() {
        this.context.setNeedValid(true);
        return this;
    }

    /**
     * Whether enable excel valid,Please use to {@link #valid(boolean)}
     *
     * @return this
     */
    @Deprecated
    public ExcelWriter closeValid() {
        this.context.setNeedValid(false);
        return this;
    }

    /**
     * Whether enable excel valid
     *
     * @param valid Enable validation
     * @return this
     */
    public ExcelWriter valid(boolean valid) {
        this.context.setNeedValid(valid);
        return this;
    }

    /**
     * Whether enable multi excel head
     *
     * @return this
     */
    @Deprecated
    public ExcelWriter enableMultiHead() {
        this.context.setMultiHead(true);
        return this;
    }

    /**
     * Whether close multi excel head, please to use {@link #multiHead(boolean)}
     *
     * @return this
     */
    @Deprecated
    public ExcelWriter closeMultiHead() {
        this.context.setMultiHead(false);
        return this;
    }

    /**
     * Whether close multi excel head
     *
     * @param multi Is multi head
     * @return this
     */
    public ExcelWriter multiHead(boolean multi) {
        this.context.setMultiHead(multi);
        return this;
    }

    /**
     * Whether you need to add a file identifier when exporting an Excel file,
     * which can be used for template validation of the file at import time
     *
     * @param identifier Need file indentifier
     * @return this
     */
    public ExcelWriter identifier(boolean identifier) {
        this.context.setIdentifier(identifier);
        return this;
    }

    /**
     * Add write listener
     *
     * @param listener Write listener
     * @return this
     */
    public ExcelWriter addListener(ExcelWriteListener listener) {
        if (listener != null) {
            this.context.addListener(listener);
        }
        return this;
    }

    /**
     * Add write listeners
     *
     * @param listeners Write listener list
     * @return this
     */
    public ExcelWriter addListener(List<? extends ExcelWriteListener> listeners) {
        if (listeners != null) {
            listeners.forEach(this.context::addListener);
        }
        return this;
    }

    /**
     * Reset the write resolver before you are ready to call the write method
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
        this.context.setExcelClass(excelClass);
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
        if (this.context.isIdentifier()) {
            this.context.getWorkbook().createSheet("identificationSheet")
                    .createRow(0)
                    .createCell(0)
                    .setCellValue(this.context.getExcelClass().getSimpleName());
            this.context.getWorkbook().setSheetHidden(this.context.getWorkbook().getSheetIndex("identificationSheet"), true);
        }
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
