package cn.gjing.tools.excel.read.resolver;

import cn.gjing.tools.excel.exception.ExcelTemplateException;
import cn.gjing.tools.excel.metadata.ExcelType;
import cn.gjing.tools.excel.metadata.ExecType;
import cn.gjing.tools.excel.metadata.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.ExcelReaderContext;
import cn.gjing.tools.excel.read.listener.ExcelResultReadListener;
import cn.gjing.tools.excel.read.resolver.core.ExcelBaseReader;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.List;

/**
 * Excel bind mode reader
 * The reader needs a mapping entity to correspond to it,
 * Automatically convert the data for each row to the corresponding Java entity
 *
 * @author Gjing
 **/
public final class ExcelBindReader<R> extends ExcelBaseReader<R> {
    public ExcelBindReader(ExcelReaderContext<R> context, InputStream inputStream, ExcelType excelType, int cacheRowSize, int bufferSize) {
        super(context, inputStream, excelType, cacheRowSize, bufferSize, ExecType.BIND);
    }

    /**
     * Read excel
     * By default, the index of the first row of Sheet is used as the index of the table head
     *
     * @return this
     */
    public ExcelBindReader<R> read() {
        super.baseReadExecutor.read(0, this.defaultSheetName);
        return this;
    }

    /**
     * Read the specified sheet
     * By default, the index of the first row of Sheet is used as the index of the table head
     *
     * @param sheetName sheet name
     * @return this
     */
    public ExcelBindReader<R> read(String sheetName) {
        super.baseReadExecutor.read(0, sheetName);
        return this;
    }

    /**
     * Specifies that the Excel subscript to start reading.
     * This line must be a real subscript,
     *
     * @param headerIndex The subscript of the table header. If there are multiple levels of table headers,
     *                    set the subscript of the bottom level of the table header. The index starts at 0
     * @return this
     */
    public ExcelBindReader<R> read(int headerIndex) {
        super.baseReadExecutor.read(headerIndex, this.defaultSheetName);
        return this;
    }

    /**
     * Read the specified sheet
     *
     * @param headerIndex The subscript of the table header. If there are multiple levels of table headers,
     *                    set the subscript of the bottom level of the table header. The index starts at 0
     * @param sheetName   Excel Sheet name
     * @return this
     */
    public ExcelBindReader<R> read(int headerIndex, String sheetName) {
        super.baseReadExecutor.read(headerIndex, sheetName);
        return this;
    }

    /**
     * Whether to read all rows before the header
     *
     * @param need Need
     * @return this
     */
    public ExcelBindReader<R> headBefore(boolean need) {
        super.context.setHeadBefore(need);
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
        super.context.setCheckTemplate(enable);
        return this;
    }

    /**
     * Check whether the imported Excel file matches the Excel mapping entity class.
     * Thrown {@link ExcelTemplateException} if there is don't match.
     *
     * @return this
     **/
    public ExcelBindReader<R> check() {
        super.context.setCheckTemplate(true);
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
        super.context.setCheckTemplate(true);
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
        super.addListenerToCache(readListenerList);
        return this;
    }

    /**
     * Add readListeners
     *
     * @param readListener Read listener
     * @return this
     */
    public ExcelBindReader<R> addListener(ExcelReadListener readListener) {
        super.addListenerToCache(readListener);
        return this;
    }

    /**
     * Subscribe to the data after the import is complete
     *
     * @param excelResultReadListener resultReadListener
     * @return this
     */
    public ExcelBindReader<R> subscribe(ExcelResultReadListener<R> excelResultReadListener) {
        super.addSubscribe(excelResultReadListener);
        return this;
    }
}
