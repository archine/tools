package cn.gjing.tools.excel.write.resolver.simple;

import cn.gjing.tools.excel.metadata.aware.ExcelWorkbookAware;
import cn.gjing.tools.excel.metadata.aware.ExcelWriteContextAware;
import cn.gjing.tools.excel.metadata.listener.DefaultExcelStyleListener;
import cn.gjing.tools.excel.metadata.listener.DefaultMultiHeadListener;
import cn.gjing.tools.excel.metadata.listener.ExcelWriteListener;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.resolver.ExcelBaseWriter;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Excel exports in simple mode, not through mapped entities
 *
 * @author Gjing
 **/
public final class ExcelSimpleWriter extends ExcelBaseWriter {

    public ExcelSimpleWriter(ExcelWriterContext context, int windowSize, HttpServletResponse response, boolean initDefaultStyle) {
        super(context, windowSize, response, initDefaultStyle);
    }

    @Override
    protected void initStyle() {
        this.addListener(new DefaultExcelStyleListener());
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
        if (headNames != null && !headNames.isEmpty()) {
            this.context.setHeadNames(headNames);
            this.context.setHeaderSeries(headNames.get(0).length);
        }
        return this;
    }

    /**
     * Set excel head row height
     *
     * @param rowHeight Row height
     * @return this
     */
    public ExcelSimpleWriter headHeight(short rowHeight) {
        this.context.setHeaderHeight(rowHeight);
        return this;
    }

    /**
     * Set excel body row height
     *
     * @param rowHeight Row height
     * @return this
     */
    public ExcelSimpleWriter bodyHeight(short rowHeight) {
        this.context.setBodyHeight(rowHeight);
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
            if (bigTitle.getLastCol() < 1) {
                bigTitle.setLastCol(this.context.getHeadNames().size() - 1);
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
        return this.write(data, this.defaultSheetName, true);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @return this
     */
    public ExcelSimpleWriter write(List<List<Object>> data, String sheetName) {
        return this.write(data, sheetName, true);
    }

    /**
     * To write
     *
     * @param data     data
     * @param needHead Whether need excel head
     * @return this
     */
    public ExcelSimpleWriter write(List<List<Object>> data, boolean needHead) {
        return this.write(data, this.defaultSheetName, needHead);
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
        this.createSheet(sheetName);
        if (data == null) {
            this.context.setTemplate(true);
            this.writerResolver.simpleWriteHead(needHead);
        } else {
            this.writerResolver.simpleWriteHead(needHead)
                    .simpleWrite(data);
        }
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
        if (enable) {
            return this.addListener(new DefaultMultiHeadListener());
        }
        return this;
    }

    /**
     * Enable multi excel head
     *
     * @return this
     */
    public ExcelSimpleWriter multiHead() {
        return this.multiHead(true);
    }

    /**
     * Add write listener
     *
     * @param listener Write listener
     * @return this
     */
    public ExcelSimpleWriter addListener(ExcelWriteListener listener) {
        this.context.addListener(listener);
        if (listener instanceof ExcelWriteContextAware) {
            ((ExcelWriteContextAware) listener).setContext(this.context);
        }
        if (listener instanceof ExcelWorkbookAware) {
            ((ExcelWorkbookAware) listener).setWorkbook(this.context.getWorkbook());
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
            listeners.forEach(this::addListener);
        }
        return this;
    }

    /**
     * Add a properties supplier
     *
     * @param supplier Properties supplier
     * @return this
     */
    public ExcelSimpleWriter supply(ExcelSimpleWriterPropSupplier supplier) {
        if (supplier != null) {
            this.context.setPropSupplier(supplier);
        }
        return this;
    }
}
