package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.metadata.ExcelWriterResolver;
import cn.gjing.tools.excel.read.resolver.ExcelReader;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.listener.ExcelWriteListener;
import cn.gjing.tools.excel.write.style.DefaultExcelStyleListener;

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
public final class ExcelBindWriter extends ExcelBaseWriter {

    public ExcelBindWriter(ExcelWriterContext context, Excel excel, HttpServletResponse response, boolean initDefaultStyle) {
        super(context, excel.windowSize(), response, initDefaultStyle);
    }

    @Override
    protected void initStyle() {
        this.context.addListener(new DefaultExcelStyleListener());
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
            if (bigTitle.getLastCols() < 1) {
                bigTitle.setLastCols(this.context.getExcelFields().size() - 1);
            }
            this.writerResolver.writeTitle(bigTitle);
        }
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
    public ExcelBindWriter resetExcelClass(Class<?> excelClass, boolean resetListener, String... ignores) {
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
        return this;
    }

    /**
     * Whether you need to add a file identifier when exporting an Excel file,
     * which can be used for template validation of the file at import time
     *
     * @param identifier Need file indentifier
     * @return this
     * @deprecated Change the method name to make it easier to understand, Used {@link #bind(boolean)}
     */
    @Deprecated
    public ExcelBindWriter identifier(boolean identifier) {
        this.context.setBind(identifier);
        return this;
    }

    /**
     * Bind the exported Excel file to the currently set mapped entity,
     * and if it is not set and detection is enabled in {@link ExcelReader#check(boolean)},
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
    public ExcelBindWriter addListener(List<? extends ExcelWriteListener> listeners) {
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
    public ExcelBindWriter resetResolver(Supplier<? extends ExcelWriterResolver> excelWriteResolver) {
        this.writerResolver = excelWriteResolver.get();
        return this;
    }

}
