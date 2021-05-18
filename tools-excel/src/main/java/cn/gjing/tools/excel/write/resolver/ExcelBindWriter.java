package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.metadata.aware.ExcelWorkbookAware;
import cn.gjing.tools.excel.metadata.aware.ExcelWriteContextAware;
import cn.gjing.tools.excel.metadata.listener.DefaultExcelStyleListener;
import cn.gjing.tools.excel.metadata.listener.DefaultMultiHeadListener;
import cn.gjing.tools.excel.metadata.listener.ExcelWriteListener;
import cn.gjing.tools.excel.read.resolver.ExcelBindReader;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.ExcelWriterContext;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Excel bind mode writer.
 * The writer needs a mapping entity to correspond to it
 *
 * @author Gjing
 * @see Excel
 **/
public final class ExcelBindWriter extends ExcelBaseWriter {

    public ExcelBindWriter(ExcelWriterContext context, Excel excel, HttpServletResponse response, boolean initDefaultStyle) {
        super(context, excel.windowSize(), response, initDefaultStyle);
    }

    @Override
    protected void initStyle() {
        this.addListener(new DefaultExcelStyleListener());
    }

    /**
     * To write
     *
     * @param data data
     * @return this
     */
    public ExcelBindWriter write(List<?> data) {
        return this.write(data, this.defaultSheetName, true, null);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @return this
     */
    public ExcelBindWriter write(List<?> data, String sheetName) {
        return this.write(data, sheetName, true, null);
    }

    /**
     * To write
     *
     * @param data     data
     * @param needHead Whether need excel head
     * @return this
     */
    public ExcelBindWriter write(List<?> data, boolean needHead) {
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
    public ExcelBindWriter write(List<?> data, String sheetName, boolean needHead) {
        return this.write(data, sheetName, needHead, null);
    }

    /**
     * To write
     *
     * @param data      data
     * @param boxValues dropdown box values
     * @return this
     */
    public ExcelBindWriter write(List<?> data, Map<String, String[]> boxValues) {
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
    public ExcelBindWriter write(List<?> data, String sheetName, Map<String, String[]> boxValues) {
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
    public ExcelBindWriter write(List<?> data, boolean needHead, Map<String, String[]> boxValues) {
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
    public ExcelBindWriter write(List<?> data, String sheetName, boolean needHead, Map<String, String[]> boxValues) {
        this.createSheet(sheetName);
        if (data == null) {
            this.context.setTemplate(true);
            this.writerResolver.writeHead(needHead, boxValues);
        } else {
            this.writerResolver.writeHead(needHead, boxValues)
                    .write(data);
        }
        return this;
    }

    /**
     * Write an Excel header that does not trigger a row callback or cell callback
     *
     * @param bigTitle Big title
     * @return this
     */
    public ExcelBindWriter writeTitle(BigTitle bigTitle) {
        return this.writeTitle(bigTitle, this.defaultSheetName);
    }

    /**
     * Write an Excel header that does not trigger a row listener or cell listener
     *
     * @param bigTitle  Big title
     * @param sheetName Sheet name
     * @return this
     */
    public ExcelBindWriter writeTitle(BigTitle bigTitle, String sheetName) {
        if (bigTitle != null) {
            this.createSheet(sheetName);
            this.writerResolver.writeTitle(bigTitle);
        }
        return this;
    }


    /**
     * Reset Excel mapped entity, Excel file name and file type are not reset
     *
     * @param excelClass Excel mapped entity
     * @param ignores    The exported field is to be ignored
     * @return this
     */
    public ExcelBindWriter resetExcelClass(Class<?> excelClass, String... ignores) {
        return this.resetExcelClass(excelClass, false, false, ignores);
    }

    /**
     * Reset Excel mapped entity, Excel file name and file type are not reset
     *
     * @param excelClass    Excel mapped entity
     * @param ignores       The exported field is to be ignored
     * @param resetListener Clear all listener caches, but do not clear listeners flagged by @ListenerNative
     * @param resetAll      Whether to delete all listeners. If false, the listeners annotated by @ListenerNative will be retained
     * @return this
     */
    public ExcelBindWriter resetExcelClass(Class<?> excelClass, boolean resetListener, boolean resetAll, String... ignores) {
        Excel excel = excelClass.getAnnotation(Excel.class);
        ParamUtils.requireNonNull(excel, "Failed to reset Excel class, the @Excel annotation was not found on the " + excelClass);
        List<String[]> headNames = new ArrayList<>();
        this.context.setExcelFields(BeanUtils.getExcelFields(excelClass, ignores, headNames));
        this.context.setHeadNames(headNames);
        if (resetListener) {
            this.context.setExcelClass(excelClass, resetAll);
        } else {
            this.context.setExcelClass(excelClass);
        }
        this.context.setBodyHeight(excel.bodyHeight());
        this.context.setHeaderHeight(excel.headerHeight());
        this.context.setHeaderSeries(headNames.get(0).length);
        this.context.setUniqueKey("".equals(excel.uniqueKey()) ? excelClass.getName() : excel.uniqueKey());
        return this;
    }

    /**
     * Enable validation annotations
     *
     * @param enable Whether validation annotations are enabled
     * @return this
     */
    public ExcelBindWriter valid(boolean enable) {
        this.context.setNeedValid(enable);
        return this;
    }

    /**
     * Enable multi excel head
     *
     * @param enable Whether enable multi excel head
     * @return this
     */
    public ExcelBindWriter multiHead(boolean enable) {
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
    public ExcelBindWriter multiHead() {
        return this.multiHead(true);
    }

    /**
     * Bind the exported Excel file to the currently set mapped entity,
     * and if it is not set and detection is enabled in {@link ExcelBindReader#check(boolean)},
     * an ExcelTemplateException will be thrown
     *
     * @param enable Whether enable bind, default true
     * @return this
     */
    public ExcelBindWriter bind(boolean enable) {
        this.context.setBind(enable);
        return this;
    }

    /**
     * Add write listener
     *
     * @param listener Write listener
     * @return this
     */
    public ExcelBindWriter addListener(ExcelWriteListener listener) {
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
    public ExcelBindWriter addListener(List<? extends ExcelWriteListener> listeners) {
        if (listeners != null) {
            listeners.forEach(this::addListener);
        }
        return this;
    }
}
