package cn.gjing.tools.excel.read;


import cn.gjing.tools.excel.Listener;
import cn.gjing.tools.excel.resolver.ExcelReaderResolver;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Excel reader
 *
 * @author Gjing
 **/
public class ExcelReader<T> implements Closeable {
    private Class<T> excelClass;
    private ExcelReaderResolver readerResolver;
    private List<T> data;
    private InputStream inputStream;
    private int headerIndex;
    private int endIndex;

    private ExcelReader() {

    }

    public ExcelReader(Class<T> excelClass, InputStream inputStream) {
        this.excelClass = excelClass;
        this.inputStream = inputStream;
        this.readerResolver = new ExcelReadResolver();
        this.data = new ArrayList<>();
        this.init();
    }

    /**
     * Initializes the sequence number
     */
    private void init() {
        this.headerIndex = 0;
        this.endIndex = 0;
    }

    /**
     * Read excel
     *
     * @return this
     */
    @SuppressWarnings("unchecked")
    public ExcelReader<T> read() {
        this.readerResolver.read(this.inputStream, this.excelClass, listener -> data = (List<T>) listener, this.headerIndex, this.endIndex, "sheet1");
        this.init();
        return this;
    }

    /**
     * Read the excel sheet
     *
     * @param sheetName sheet name
     * @return this
     */
    @SuppressWarnings("unchecked")
    public ExcelReader<T> read(String sheetName) {
        this.readerResolver.read(this.inputStream, this.excelClass, listener -> data = (List<T>) listener, this.headerIndex, this.endIndex, sheetName);
        this.init();
        return this;
    }

    /**
     * Reset the processor before any other operation
     *
     * @param excelReaderResolver Excel read resolver
     * @return this
     */
    public ExcelReader<T> resetResolver(Supplier<? extends ExcelReaderResolver> excelReaderResolver) {
        this.readerResolver = excelReaderResolver.get();
        return this;
    }

    /**
     * Excel header index
     *
     * @param index List header, which is the number to the left of the excel file list header
     * @return this
     */
    public ExcelReader<T> headerIndex(int index) {
        this.headerIndex = index - 1;
        return this;
    }

    /**
     * Read the cutoff index
     *
     * @param index cutoff index
     * @return this
     */
    public ExcelReader<T> endIndex(int index) {
        this.endIndex = index;
        return this;
    }

    /**
     * Get result
     *
     * @return List
     */
    public List<T> get() {
        return this.data;
    }

    /**
     * Listens for the return of the result through the listener
     *
     * @param resultListener Result listener
     * @return this
     */
    public ExcelReader<T> listener(Listener<List<T>> resultListener) {
        resultListener.notify(this.data);
        return this;
    }

    @Override
    public void close() throws IOException {
        if (this.inputStream != null) {
            this.inputStream.close();
        }
    }
}
