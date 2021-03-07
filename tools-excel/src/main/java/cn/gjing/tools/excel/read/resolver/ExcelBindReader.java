package cn.gjing.tools.excel.read.resolver;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.read.ExcelReaderContext;
import cn.gjing.tools.excel.read.listener.ExcelEmptyReadListener;
import cn.gjing.tools.excel.read.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.listener.ExcelResultReadListener;
import cn.gjing.tools.excel.read.listener.ExcelRowReadListener;
import cn.gjing.tools.excel.util.ParamUtils;

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
     * Detect whether the imported Excel file matches the mapped entity
     *
     * @param enable Whether enable check
     * @return this
     */
    public ExcelBindReader<R> check(boolean enable) {
        this.context.setCheckTemplate(enable);
        return this;
    }

    /**
     * Add readListeners
     *
     * @param readListenerList Read listeners
     * @return this
     */
    public ExcelBindReader<R> addListener(List<ExcelReadListener> readListenerList) {
        if (readListenerList != null) {
            readListenerList.forEach(this.context::addListener);
        }
        return this;
    }

    /**
     * Add readListeners
     *
     * @param readListener Read listener
     * @return this
     */
    public ExcelBindReader<R> addListener(ExcelReadListener readListener) {
        if (readListener != null) {
            this.context.addListener(readListener);
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

    /**
     * Remove read empty listener
     *
     * @return this
     */
    public ExcelBindReader<R> removeEmptyListener() {
        ParamUtils.deleteMapKey(this.context.getReadListenersCache(), ExcelEmptyReadListener.class);
        return this;
    }

    /**
     * Remove excel read row listener
     *
     * @return this
     */
    public ExcelBindReader<R> removeRowListener() {
        ParamUtils.deleteMapKey(this.context.getReadListenersCache(), ExcelRowReadListener.class);
        return this;
    }

    /**
     * Remove excel read result listener
     *
     * @return this
     */
    public ExcelBindReader<R> removeResultListener() {
        this.context.setResultReadListener(null);
        return this;
    }
}
