package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.metadata.ExcelFieldProperty;
import cn.gjing.tools.excel.metadata.ExecType;
import cn.gjing.tools.excel.metadata.aware.ExcelWorkbookAware;
import cn.gjing.tools.excel.metadata.aware.ExcelWriteContextAware;
import cn.gjing.tools.excel.metadata.listener.DefaultExcelStyleListener;
import cn.gjing.tools.excel.metadata.listener.DefaultMultiHeadListener;
import cn.gjing.tools.excel.metadata.listener.ExcelWriteListener;
import cn.gjing.tools.excel.read.resolver.ExcelBindReader;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel exports in simple mode, not through mapped entities
 *
 * @author Gjing
 **/
public final class ExcelSimpleWriter extends ExcelBaseWriter {

    public ExcelSimpleWriter(ExcelWriterContext context, int windowSize, HttpServletResponse response, boolean initDefaultStyle) {
        super(context, windowSize, response, initDefaultStyle, ExecType.SIMPLE);
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
            this.context.setHeaderSeries(headNames.get(0).length);
            List<ExcelFieldProperty> properties = new ArrayList<>();
            for (String[] headName : headNames) {
                properties.add(ExcelFieldProperty.builder()
                        .value(headName)
                        .build());
            }
            this.context.setFieldProperties(properties);
        }
        return this;
    }

    /**
     * Set the Excel property
     *
     * @param properties Excel filed property
     * @return this
     */
    public ExcelSimpleWriter head2(List<ExcelFieldProperty> properties) {
        if (properties != null && !properties.isEmpty()) {
            this.context.setFieldProperties(properties);
            this.context.setHeaderSeries(properties.get(0).getValue().length);
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
                bigTitle.setLastCol(this.context.getFieldProperties().size() - 1);
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
            this.writerResolver.writeHead(needHead, null);
        } else {
            this.writerResolver.writeHead(needHead, null)
                    .write(data);
        }
        return this;
    }

    /**
     * Is multi excel head
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
     * Whether to set the exported file as a template file when the data is null
     *
     * @param isTemp True means to set the exported file to a template file
     * @return this
     */
    public ExcelSimpleWriter temp(boolean isTemp) {
        super.nullIsTemp = isTemp;
        return this;
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
     * Bind the exported Excel file to the currently set unique key,
     * Can be used to {@link ExcelBindReader#check} for a match with an entity class when a file is imported.
     *
     * @param key Unique key ,Each exported file recommends that the key be set to be unique
     * @return this
     */
    public ExcelSimpleWriter bind(String key) {
        if (!StringUtils.isEmpty(key)) {
            this.context.setBind(true);
            this.context.setUniqueKey(key);
        }
        return this;
    }

    /**
     * Unbind the unique key of the file
     *
     * @return this
     */
    public ExcelSimpleWriter unbind() {
        this.context.setBind(false);
        return this;
    }
}
