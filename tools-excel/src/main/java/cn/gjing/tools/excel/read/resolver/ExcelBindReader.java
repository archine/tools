package cn.gjing.tools.excel.read.resolver;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.exception.ExcelTemplateException;
import cn.gjing.tools.excel.metadata.aware.ExcelReaderContextAware;
import cn.gjing.tools.excel.metadata.aware.ExcelWorkbookAware;
import cn.gjing.tools.excel.metadata.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.ExcelReaderContext;
import cn.gjing.tools.excel.read.listener.ExcelResultReadListener;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.List;

/**
 * Excel bind mode reader
 * The reader needs a mapping entity to correspond to it
 *
 * @author Gjing
 **/
public final class ExcelBindReader<R> extends ExcelBaseReader<R> {
    public ExcelBindReader(ExcelReaderContext<R> context, InputStream inputStream, Excel excel) {
        super(context, inputStream, excel);
    }

    /**
     * Read excel
     *
     * @return this
     */
    public ExcelBindReader<R> read() {
        this.readerResolver.read(0, this.defaultSheetName);
        return this;
    }

    /**
     * Read the specified sheet
     *
     * @param sheetName sheet name
     * @return this
     */
    public ExcelBindReader<R> read(String sheetName) {
        this.readerResolver.read(0, sheetName);
        return this;
    }

    /**
     * Specifies that the Excel subscript to start reading.
     * This line must be a real subscript,
     *
     * @param headerIndex The actual subscript of the Excel header,
     *                    subscript is evaluated from 0
     * @return this
     */
    public ExcelBindReader<R> read(int headerIndex) {
        this.readerResolver.read(headerIndex, this.defaultSheetName);
        return this;
    }

    /**
     * Read the specified sheet
     *
     * @param headerIndex The actual subscript of the Excel header,
     *                    subscript is evaluated from 0
     * @param sheetName   Excel Sheet name
     * @return this
     */
    public ExcelBindReader<R> read(int headerIndex, String sheetName) {
        this.readerResolver.read(headerIndex, sheetName);
        return this;
    }

    /**
     * Read rows before the header
     *
     * @param need Need
     * @return this
     * @deprecated Please use headBefore
     */
    @Deprecated
    public ExcelBindReader<R> metaInfo(boolean need) {
        return this.headBefore(need);
    }

    /**
     * Whether to read all rows before the header
     *
     * @param need Need
     * @return this
     */
    public ExcelBindReader<R> headBefore(boolean need) {
        this.context.setHeadBefore(need);
        return this;
    }

    /**
     * Detect whether the imported Excel file matches the mapped entity.
     * Thrown ExcelTemplateException if there is don't match.
     *
     * @param enable Whether enable check
     * @return this
     * @deprecated Please use {@link #check()}
     */
    @Deprecated
    public ExcelBindReader<R> check(boolean enable) {
        this.context.setCheckTemplate(enable);
        return this;
    }

    /**
     * Check whether the imported Excel file matches the Excel mapping entity class.
     * Thrown {@link ExcelTemplateException} if there is don't match.
     *
     * @return this
     **/
    public ExcelBindReader<R> check() {
        this.context.setCheckTemplate(true);
        return this;
    }

    /**
     * Check whether the imported Excel file matches the Excel mapping entity class.
     * Thrown {@link ExcelTemplateException} if there is don't match.
     *
     * @param key Unique key
     * @return this
     **/
    public ExcelBindReader<R> check(String key) {
        this.context.setCheckTemplate(true);
        if (!StringUtils.isEmpty(key)) {
            this.context.setUniqueKey(key);
        }
        return this;
    }

    /**
     * Add readListeners
     *
     * @param readListenerList Read listeners
     * @return this
     */
    public ExcelBindReader<R> addListener(List<? extends ExcelReadListener> readListenerList) {
        if (readListenerList != null) {
            readListenerList.forEach(this::addListener);
        }
        return this;
    }

    /**
     * Add readListeners
     *
     * @param readListener Read listener
     * @return this
     */
    @SuppressWarnings("unchecked")
    public ExcelBindReader<R> addListener(ExcelReadListener readListener) {
        this.context.addListener(readListener);
        if (readListener instanceof ExcelReaderContextAware) {
            ((ExcelReaderContextAware<R>) readListener).setContext(this.context);
        }
        if (readListener instanceof ExcelWorkbookAware) {
            ((ExcelWorkbookAware) readListener).setWorkbook(this.context.getWorkbook());
        }
        return this;
    }

    /**
     * Subscribe to the data after the import is complete
     *
     * @param excelResultReadListener resultReadListener
     * @return this
     */
    public ExcelBindReader<R> subscribe(ExcelResultReadListener<R> excelResultReadListener) {
        this.context.setResultReadListener(excelResultReadListener);
        return this;
    }
}
