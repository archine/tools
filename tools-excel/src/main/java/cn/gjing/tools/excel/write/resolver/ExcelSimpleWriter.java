package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.metadata.ExcelWriterResolver;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.callback.ExcelAutoMergeCallback;
import cn.gjing.tools.excel.write.listener.ExcelWriteListener;
import cn.gjing.tools.excel.write.style.DefaultExcelStyleListener;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Excel exports in simple mode, not through mapped entities
 *
 * @author Gjing
 **/
public final class ExcelSimpleWriter extends ExcelBaseWriter {
    private boolean mergeEmpty = true;

    public ExcelSimpleWriter(ExcelWriterContext context, int windowSize, HttpServletResponse response, boolean initDefaultStyle) {
        super(context, windowSize, response, initDefaultStyle);
    }

    @Override
    protected void initStyle() {
        this.context.addListener(new DefaultExcelStyleListener());
    }

    /**
     * Set the Excel header
     *
     * @param headNames Excel header name arrays, According to the first header array
     *                  size to determine the header hierarchy,
     *                  the subsequent header array must be the same size as the first
     * @return this
     */
    public ExcelSimpleWriter head(List<String[]> headNames) {
        this.context.setHeadNames(headNames);
        return this;
    }

    /**
     * Write an Excel header that does not trigger a row callback or cell callback
     *
     * @param bigTitle Big title
     * @return this
     */
    public ExcelSimpleWriter writeTitle(BigTitle bigTitle) {
        return this.writeTitle(bigTitle, this.defaultSheetName);
    }

    /**
     * Write an Excel header that does not trigger a row listener or cell listener
     *
     * @param bigTitle  Big title
     * @param sheetName Sheet name
     * @return this
     */
    public ExcelSimpleWriter writeTitle(BigTitle bigTitle, String sheetName) {
        if (bigTitle != null) {
            this.createSheet(sheetName);
            if (bigTitle.getLastCols() < 1) {
                bigTitle.setLastCols(this.context.getHeadNames().size() - 1);
            }
            this.writerResolver.writeTitle(bigTitle);
        }
        return this;
    }

    /**
     * To write
     *
     * @param data Sequential padding, which needs to correspond to the header sequence
     * @return this
     */
    public ExcelSimpleWriter write(List<List<Object>> data) {
        return this.write(data, this.defaultSheetName, true, null);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @return this
     */
    public ExcelSimpleWriter write(List<List<Object>> data, String sheetName) {
        return this.write(data, sheetName, true, null);
    }

    /**
     * To write
     *
     * @param data     data
     * @param needHead Whether need excel head
     * @return this
     */
    public ExcelSimpleWriter write(List<List<Object>> data, boolean needHead) {
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
    public ExcelSimpleWriter write(List<List<Object>> data, String sheetName, boolean needHead) {
        return this.write(data, sheetName, needHead, null);
    }

    /**
     * To write
     *
     * @param data     data
     * @param callback Merge callbacks at export time
     * @return this
     */
    public ExcelSimpleWriter write(List<List<Object>> data, Map<String, ExcelAutoMergeCallback<?>> callback) {
        return this.write(data, this.defaultSheetName, true, callback);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @param callback  Merge callbacks at export time
     * @return this
     */
    public ExcelSimpleWriter write(List<List<Object>> data, String sheetName, Map<String, ExcelAutoMergeCallback<?>> callback) {
        return this.write(data, sheetName, true, callback);
    }

    /**
     * To write
     *
     * @param data     data
     * @param callback Merge callbacks at export time
     * @param needHead Whether need excel head
     * @return this
     */
    public ExcelSimpleWriter write(List<List<Object>> data, boolean needHead, Map<String, ExcelAutoMergeCallback<?>> callback) {
        return this.write(data, this.defaultSheetName, needHead, callback);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @param callback  Merge callbacks at export time
     * @param needHead  Whether need excel head
     * @return this
     */
    public ExcelSimpleWriter write(List<List<Object>> data, String sheetName, boolean needHead, Map<String, ExcelAutoMergeCallback<?>> callback) {
        this.createSheet(sheetName);
        this.writerResolver.simpleWriteHead(needHead)
                .simpleWrite(data, this.mergeEmpty, callback == null ? new HashMap<>(2) : callback);
        return this;
    }

    /**
     * Whether null data is allowed to initiate a merge callback
     *
     * @param mergeEmpty mergeEmpty
     * @return this
     */
    public ExcelSimpleWriter mergeEmpty(boolean mergeEmpty) {
        this.mergeEmpty = mergeEmpty;
        return this;
    }

    /**
     * Enable multi excel head
     *
     * @param enable Whether enable multi excel head
     * @return this
     */
    public ExcelSimpleWriter multiHead(boolean enable) {
        this.context.setMultiHead(enable);
        return this;
    }

    /**
     * Add write listener
     *
     * @param listener Write listener
     * @return this
     */
    public ExcelSimpleWriter addListener(ExcelWriteListener listener) {
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
    public ExcelSimpleWriter addListener(List<? extends ExcelWriteListener> listeners) {
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
    public ExcelSimpleWriter resetResolver(Supplier<? extends ExcelWriterResolver> excelWriteResolver) {
        this.writerResolver = excelWriteResolver.get();
        return this;
    }
}
