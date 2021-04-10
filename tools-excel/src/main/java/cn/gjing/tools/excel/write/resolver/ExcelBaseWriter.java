package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.metadata.annotation.ListenerNative;
import cn.gjing.tools.excel.metadata.listener.ExcelWriteListener;
import cn.gjing.tools.excel.metadata.resolver.ExcelWriterResolver;
import cn.gjing.tools.excel.read.resolver.ExcelBindReader;
import cn.gjing.tools.excel.util.ListenerChain;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.listener.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
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
     * @param context    Excel write context
     * @param windowSize Window size, which is flushed to disk when exported
     *                   if the data that has been written out exceeds the specified size
     */
    protected void chooseResolver(ExcelWriterContext context, int windowSize) {
        switch (this.context.getExcelType()) {
            case XLS:
                context.setWorkbook(new HSSFWorkbook());
                this.writerResolver = new ExcelWriteXlsResolver(context);
                break;
            case XLSX:
                context.setWorkbook(new SXSSFWorkbook(windowSize));
                this.writerResolver = new ExcelWriteXlsxResolver(context);
                break;
            default:
        }
        ListenerChain.doWorkbookCreated(this.context.getWriteListenerCache().get(ExcelWorkbookWriteListener.class), this.context.getWorkbook());
    }

    /**
     * Flush all content to excel of the cache
     */
    public void flush() {
        ListenerChain.doWorkbookFlushBefore(this.context.getWriteListenerCache().get(ExcelWorkbookWriteListener.class), this.context.getWorkbook());
        this.writerResolver.flush(this.response, this.context);
        if (this.context.getWorkbook() instanceof SXSSFWorkbook) {
            ((SXSSFWorkbook) this.context.getWorkbook()).dispose();
        }
    }

    /**
     * Flush all content to excel of the cache to local
     *
     * @param path Absolute path to the directory where the file is stored
     */
    public void flushToLocal(String path) {
        ListenerChain.doWorkbookFlushBefore(this.context.getWriteListenerCache().get(ExcelWorkbookWriteListener.class), this.context.getWorkbook());
        this.writerResolver.flushToLocal(path, this.context);
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
            ListenerChain.doCompleteSheet(this.context.getWriteListenerCache().get(ExcelSheetWriteListener.class), sheet);
            if (this.context.isBind()) {
                this.bind();
            }
        }
    }

    /**
     * Bind the exported Excel file to the currently set mapped entity,
     * and if it is not set and detection is enabled in {@link ExcelBindReader#check(boolean)},
     * an ExcelTemplateException will be thrown
     **/
    private void bind() {
        String sheet = "unq-" + this.context.getSheet().getSheetName();
        Sheet extensionSheet = this.context.getWorkbook().createSheet(sheet);
        extensionSheet.protectSheet(UUID.randomUUID().toString().replaceAll("-", ""));
        Row extensionSheetRow = extensionSheet.createRow(0);
        extensionSheetRow.createCell(0).setCellValue(ParamUtils.encodeMd5(this.context.getUniqueKey()));
        this.context.getWorkbook().setSheetHidden(this.context.getWorkbook().getSheetIndex(sheet), true);
    }

    /**
     * Add write listener to cache
     *
     * @param excelWriteListener Read listener
     */
    protected void addListenerCache(ExcelWriteListener excelWriteListener) {
        if (excelWriteListener instanceof ExcelStyleWriteListener) {
            List<ExcelWriteListener> listeners = this.context.getWriteListenerCache().computeIfAbsent(ExcelStyleWriteListener.class, k -> new ArrayList<>());
            ((ExcelStyleWriteListener) excelWriteListener).init(this.context.getWorkbook());
            listeners.add(excelWriteListener);
        }
        if (excelWriteListener instanceof ExcelSheetWriteListener) {
            List<ExcelWriteListener> listeners = this.context.getWriteListenerCache().computeIfAbsent(ExcelSheetWriteListener.class, k -> new ArrayList<>());
            listeners.add(excelWriteListener);
        }
        if (excelWriteListener instanceof ExcelRowWriteListener) {
            List<ExcelWriteListener> listeners = this.context.getWriteListenerCache().computeIfAbsent(ExcelRowWriteListener.class, k -> new ArrayList<>());
            listeners.add(excelWriteListener);
        }
        if (excelWriteListener instanceof ExcelCellWriteListener) {
            List<ExcelWriteListener> listeners = this.context.getWriteListenerCache().computeIfAbsent(ExcelCellWriteListener.class, k -> new ArrayList<>());
            listeners.add(excelWriteListener);
        }
        if (excelWriteListener instanceof ExcelCascadingDropdownBoxListener) {
            List<ExcelWriteListener> listeners = this.context.getWriteListenerCache().computeIfAbsent(ExcelCascadingDropdownBoxListener.class, k -> new ArrayList<>());
            listeners.add(excelWriteListener);
        }
        if (excelWriteListener instanceof ExcelWorkbookWriteListener) {
            List<ExcelWriteListener> listeners = this.context.getWriteListenerCache().computeIfAbsent(ExcelWorkbookWriteListener.class, k -> new ArrayList<>());
            listeners.add(excelWriteListener);
        }
        if (excelWriteListener instanceof ExcelWriteContextListener) {
            List<ExcelWriteListener> listeners = this.context.getWriteListenerCache().computeIfAbsent(ExcelWriteContextListener.class, k -> new ArrayList<>());
            listeners.add(excelWriteListener);
            ((ExcelWriteContextListener) excelWriteListener).setContext(this.context);
        }
    }

    /**
     * Deletes the current listener cache, save the listener marked with the @ListenerNative annotation
     *
     * @param all Whether to delete listeners flagged by @ListenerNative
     * @param key { ExcelStyleWriteListener.class
     *            ExcelCascadingDropdownBoxListener.class
     *            ExcelCellWriteListener.class
     *            ExcelRowWriteListener.class
     *            ExcelSheetWriteListener.class
     *            ExcelWorkbookWriteListener.class
     *            ExcelWriteContextListener.class
     *            }
     */
    protected void delListenerCache(Class<? extends ExcelWriteListener> key, boolean all) {
        List<ExcelWriteListener> excelWriteListeners = this.context.getWriteListenerCache().get(key);
        if (excelWriteListeners == null || excelWriteListeners.isEmpty()) {
            return;
        }
        if (all) {
            excelWriteListeners.clear();
        } else {
            excelWriteListeners.removeIf(e -> {
                ListenerNative listenerNative = e.getClass().getAnnotation(ListenerNative.class);
                if (listenerNative == null) {
                    return true;
                }
                for (Class<?> aClass : listenerNative.value()) {
                    if (this.context.getExcelClass() == aClass) {
                        return true;
                    }
                }
                return false;
            });
        }
    }
}
