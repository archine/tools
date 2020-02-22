package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.Listener;
import cn.gjing.tools.excel.resolver.ExcelReaderResolver;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Excel reader
 *
 * @author Gjing
 **/
public class ExcelReader<T> {
    private Class<T> excelClass;
    private ExcelReaderResolver readerResolver;
    private List<T> data;
    private InputStream inputStream;
    private int headerIndex;
    private int readLines;

    private ExcelReader() {

    }

    public ExcelReader(Class<T> excelClass, InputStream inputStream) {
        this.excelClass = excelClass;
        this.inputStream = inputStream;
        try (final DefaultExcelReadResolver excelReadResolver = new DefaultExcelReadResolver()) {
            this.readerResolver = excelReadResolver;
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.data = new ArrayList<>();
        this.init();
    }

    /**
     * Initializes the sequence number
     */
    private void init() {
        this.headerIndex = 0;
        this.readLines = 0;
    }

    /**
     * Read excel
     *
     * @return this
     */
    @SuppressWarnings("unchecked")
    public ExcelReader<T> read() {
        this.readerResolver.read(this.inputStream, this.excelClass, listener -> data = (List<T>) listener, this.headerIndex, this.readLines, "sheet1");
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
        this.readerResolver.read(this.inputStream, this.excelClass, listener -> data = (List<T>) listener, this.headerIndex, this.readLines, sheetName);
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
     * Excel header starter index
     *
     * @param index List header, which is the number to the left of the excel file list header
     * @return this
     */
    public ExcelReader<T> headerIndex(int index) {
        this.headerIndex = index;
        return this;
    }

    /**
     * Read how many rows
     *
     * @param lines Number of lines read
     * @return this
     */
    public ExcelReader<T> readLines(int lines) {
        this.readLines = lines;
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
    public ExcelReader<T> subscribe(Listener<List<T>> resultListener) {
        resultListener.notify(this.data);
        return this;
    }
}
