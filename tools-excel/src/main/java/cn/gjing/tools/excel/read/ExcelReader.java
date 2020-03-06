package cn.gjing.tools.excel.read;


import cn.gjing.tools.excel.listen.ReadCallback;
import cn.gjing.tools.excel.listen.ReadListener;
import cn.gjing.tools.excel.resolver.ExcelReaderResolver;

import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

/**
 * Excel reader
 *
 * @author Gjing
 **/
public class ExcelReader<R> {
    private Class<R> excelClass;
    private List<R> data;
    private ReadCallback<R> readCallback;
    private ExcelReaderResolver<R> readerResolver;
    private InputStream inputStream;
    private int headerIndex;
    private int readLines;

    private ExcelReader() {

    }

    public ExcelReader(Class<R> excelClass, InputStream inputStream) {
        this.excelClass = excelClass;
        this.inputStream = inputStream;
        try (final DefaultExcelReadResolver<R> excelReadResolver = new DefaultExcelReadResolver<>()) {
            this.readerResolver = excelReadResolver;
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.readCallback = (R, rowIndex) -> R;
        this.initSequence();
    }

    /**
     * Initializes the sequence number and resolver
     */
    private void initSequence() {
        this.headerIndex = 0;
        this.readLines = 0;
    }

    /**
     * Read excel
     *
     * @return this
     */
    public ExcelReader<R> read() {
        this.readerResolver.read(this.inputStream, this.excelClass, readData -> data = readData, this.headerIndex, this.readLines,
                "sheet1", this.readCallback);
        this.initSequence();
        return this;
    }

    /**
     * Read excel
     *
     * @param callback Excel import callback
     * @return this
     */
    public ExcelReader<R> read(Supplier<? extends ReadCallback<R>> callback) {
        this.readerResolver.read(this.inputStream, this.excelClass, readData -> data = readData, this.headerIndex, this.readLines,
                "sheet1", callback.get());
        this.initSequence();
        return this;
    }

    /**
     * Read the excel sheet
     *
     * @param sheetName sheet name
     * @return this
     */
    public ExcelReader<R> read(String sheetName) {
        this.readerResolver.read(this.inputStream, this.excelClass, readData -> data = readData, this.headerIndex, this.readLines,
                sheetName, this.readCallback);
        this.initSequence();
        return this;
    }

    /**
     * Read the excel sheet
     *
     * @param sheetName sheet name
     * @param callback  Excel import callback
     * @return this
     */
    public ExcelReader<R> read(String sheetName, Supplier<? extends ReadCallback<R>> callback) {
        this.readerResolver.read(this.inputStream, this.excelClass, readData -> data = readData, this.headerIndex, this.readLines,
                sheetName, callback.get());
        this.initSequence();
        return this;
    }

    /**
     * Reset the processor before any other operation
     *
     * @param excelReaderResolver Excel read resolver
     * @return this
     */
    public ExcelReader<R> resetResolver(Supplier<? extends ExcelReaderResolver<R>> excelReaderResolver) {
        this.readerResolver = excelReaderResolver.get();
        return this;
    }

    /**
     * Excel header starter index
     *
     * @param index List header, which is the number to the left of the excel file list header
     * @return this
     */
    public ExcelReader<R> headerIndex(int index) {
        this.headerIndex = index;
        return this;
    }

    /**
     * Read how many rows
     *
     * @param lines Number of lines read
     * @return this
     */
    public ExcelReader<R> readLines(int lines) {
        this.readLines = lines;
        return this;
    }

    /**
     * Get result
     *
     * @return List
     */
    public List<R> get() {
        return this.data;
    }

    /**
     * Listens for the return of the result through the listener
     *
     * @param dataReadListener Result listener
     * @return this
     */
    public ExcelReader<R> subscribe(ReadListener<List<R>> dataReadListener) {
        dataReadListener.notify(this.data);
        return this;
    }
}
