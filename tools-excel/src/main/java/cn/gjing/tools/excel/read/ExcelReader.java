package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.metadata.ReadCallback;
import cn.gjing.tools.excel.metadata.ReadListener;
import cn.gjing.tools.excel.metadata.ExcelReaderResolver;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;

/**
 * Excel reader
 *
 * @author Gjing
 **/
public class ExcelReader<R> {
    private Class<R> excelClass;
    private Excel excel;
    private List<Field> excelFieldList;
    private List<R> data;
    private ReadCallback<R> readCallback;
    private ExcelReaderResolver<R> readerResolver;
    private InputStream inputStream;
    private Workbook workbook;
    private int headerIndex;
    private int readLines;
    private String defaultSheetName = "sheet1";

    private ExcelReader() {

    }

    public ExcelReader(Class<R> excelClass, InputStream inputStream, Excel excel, List<Field> excelFieldList) {
        this.excelClass = excelClass;
        this.inputStream = inputStream;
        this.readCallback = (r, rowIndex) -> r;
        this.excel = excel;
        this.excelFieldList = excelFieldList;
        this.initResolver(excel, inputStream);
        this.initSequence();
    }

    /**
     * Init excel read resolver
     *
     * @param excel       Excel annotation of Excel entity
     * @param inputStream File inputStream
     */
    private void initResolver(Excel excel, InputStream inputStream) {
        this.readerResolver = new DefaultExcelReadResolver<>();
        switch (excel.type()) {
            case XLS:
                try {
                    this.workbook = new HSSFWorkbook(inputStream);
                } catch (IOException e) {
                    throw new ExcelInitException("Init workbook error, " + e.getMessage());
                }
                break;
            case XLSX:
                this.workbook = StreamingReader.builder().rowCacheSize(excel.cacheRowSize()).bufferSize(excel.bufferSize()).open(inputStream);
                break;
            default:
                throw new ExcelInitException("No corresponding processor was found");
        }
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
        this.readerResolver.read(this.inputStream, this.excelClass, readData -> this.data = readData, this.headerIndex, this.readLines,
                this.defaultSheetName, this.readCallback, this.workbook, this.excelFieldList, excel);
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
        this.readerResolver.read(this.inputStream, this.excelClass, readData -> this.data = readData, this.headerIndex, this.readLines,
                this.defaultSheetName, callback.get(), this.workbook, this.excelFieldList, excel);
        this.initSequence();
        return this;
    }

    /**
     * Read the specified sheet
     *
     * @param sheetName sheet name
     * @return this
     */
    public ExcelReader<R> read(String sheetName) {
        this.readerResolver.read(this.inputStream, this.excelClass, readData -> this.data = readData, this.headerIndex, this.readLines,
                sheetName, this.readCallback, this.workbook, this.excelFieldList, excel);
        this.initSequence();
        return this;
    }

    /**
     * Read the specified sheet
     *
     * @param sheetName sheet name
     * @param callback  Excel import callback
     * @return this
     */
    public ExcelReader<R> read(String sheetName, Supplier<? extends ReadCallback<R>> callback) {
        this.readerResolver.read(this.inputStream, this.excelClass, readData -> this.data = readData, this.headerIndex, this.readLines,
                sheetName, callback.get(), this.workbook, this.excelFieldList, excel);
        this.initSequence();
        return this;
    }

    /**
     * Read the excel sheet
     *
     * @param headerIndex Excel header starter index
     * @return this
     */
    public ExcelReader<R> read(int headerIndex) {
        this.readerResolver.read(this.inputStream, this.excelClass, readData -> this.data = readData, headerIndex, this.readLines,
                this.defaultSheetName, this.readCallback, this.workbook, this.excelFieldList, excel);
        this.initSequence();
        return this;
    }

    /**
     * Read the specified sheet
     *
     * @param headerIndex Excel header starter index
     * @param callback    Excel import callback
     * @return this
     */
    public ExcelReader<R> read(int headerIndex, Supplier<? extends ReadCallback<R>> callback) {
        this.readerResolver.read(this.inputStream, this.excelClass, readData -> this.data = readData, headerIndex, this.readLines,
                this.defaultSheetName, callback.get(), this.workbook, this.excelFieldList, excel);
        this.initSequence();
        return this;
    }

    /**
     * Read the specified sheet
     *
     * @param headerIndex Excel header starter index
     * @param sheetName   Excel Sheet name
     * @return this
     */
    public ExcelReader<R> read(int headerIndex, String sheetName) {
        this.readerResolver.read(this.inputStream, this.excelClass, readData -> this.data = readData, headerIndex, this.readLines,
                sheetName, this.readCallback, this.workbook, this.excelFieldList, excel);
        this.initSequence();
        return this;
    }

    /**
     * Read the specified sheet
     *
     * @param headerIndex Excel header starter index
     * @param callback    Excel import callback
     * @param sheetName   Sheet name
     * @return this
     */
    public ExcelReader<R> read(int headerIndex, String sheetName, Supplier<? extends ReadCallback<R>> callback) {
        this.readerResolver.read(this.inputStream, this.excelClass, readData -> this.data = readData, headerIndex, this.readLines,
                sheetName, callback.get(), this.workbook, this.excelFieldList, excel);
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
    @Deprecated
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
        this.end();
        return this.data;
    }

    /**
     * Listen for the return of the result through the listener
     *
     * @param dataReadListener Result listener
     * @return this
     */
    public ExcelReader<R> subscribe(ReadListener<List<R>> dataReadListener) {
        dataReadListener.notify(this.data);
        this.data.clear();
        return this;
    }

    /**
     * Listen for the return of the result through the listener
     *
     * @param dataReadListener Result listener
     * @return this
     */
    public ExcelReader<R> subscribe(ReadListener<List<R>> dataReadListener, boolean clear) {
        dataReadListener.notify(this.data);
        if (clear) {
            this.data.clear();
        }
        return this;
    }

    /**
     * Excel import end
     */
    public void end() {
        try {
            if (this.inputStream != null) {
                this.inputStream.close();
            }
            if (this.workbook != null) {
                this.workbook.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
