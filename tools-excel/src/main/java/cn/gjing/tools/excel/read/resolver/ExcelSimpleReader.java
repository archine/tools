package cn.gjing.tools.excel.read.resolver;

import cn.gjing.tools.excel.metadata.ExcelType;
import cn.gjing.tools.excel.metadata.ExecType;
import cn.gjing.tools.excel.metadata.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.ExcelReaderContext;
import cn.gjing.tools.excel.read.resolver.core.ExcelBaseReader;

import java.io.InputStream;
import java.util.List;

/**
 * Excel simple mode reader
 * No mapping entities need to be provided.
 * Instead of automatically turning each row into a Java entity,
 * you can manually assemble your own objects in {@link cn.gjing.tools.excel.read.listener.ExcelRowReadListener}
 *
 * @author Gjing
 **/
public class ExcelSimpleReader<R> extends ExcelBaseReader<R> {
    public ExcelSimpleReader(ExcelReaderContext<R> context, InputStream inputStream, ExcelType excelType, int cacheRowSize, int bufferSize) {
        super(context, inputStream, excelType, cacheRowSize, bufferSize, ExecType.SIMPLE);
    }

    /**
     * Read excel
     * By default, the index of the first row of Sheet is used as the index of the table head
     *
     * @return this
     */
    public ExcelSimpleReader<R> read() {
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
    public ExcelSimpleReader<R> read(String sheetName) {
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
    public ExcelSimpleReader<R> read(int headerIndex) {
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
    public ExcelSimpleReader<R> read(int headerIndex, String sheetName) {
        super.baseReadExecutor.read(headerIndex, sheetName);
        return this;
    }

    /**
     * Whether to read all rows before the header
     *
     * @param need Need
     * @return this
     */
    public ExcelSimpleReader<R> headBefore(boolean need) {
        super.context.setHeadBefore(need);
        return this;
    }

    /**
     * Add excel read listener
     *
     * @param readListenerList Read listeners
     * @return this
     */
    public ExcelSimpleReader<R> addListener(List<? extends ExcelReadListener> readListenerList) {
        super.addListenerToCache(readListenerList);
        return this;
    }

    /**
     * Add excel read listener
     *
     * @param readListener Read listener
     * @return this
     */
    public ExcelSimpleReader<R> addListener(ExcelReadListener readListener) {
        super.addListenerToCache(readListener);
        return this;
    }
}
