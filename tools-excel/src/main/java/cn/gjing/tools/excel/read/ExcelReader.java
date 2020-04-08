package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.metadata.ExcelReaderResolver;
import cn.gjing.tools.excel.read.listener.EmptyReadListener;
import cn.gjing.tools.excel.read.listener.ReadListener;
import cn.gjing.tools.excel.read.listener.ResultReadListener;
import cn.gjing.tools.excel.read.listener.RowReadListener;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Excel reader
 *
 * @author Gjing
 **/
public class ExcelReader<R> {
    private Class<R> excelClass;
    private List<Field> excelFieldList;
    private ExcelReaderResolver<R> readerResolver;
    private InputStream inputStream;
    private Workbook workbook;
    private boolean collect;
    private String defaultSheetName = "sheet1";
    private Map<Class<? extends ReadListener<R>>, List<ReadListener<R>>> readListeners;

    private ExcelReader() {

    }

    public ExcelReader(Class<R> excelClass, InputStream inputStream, Excel excel, List<Field> excelFieldList) {
        this.excelClass = excelClass;
        this.inputStream = inputStream;
        this.excelFieldList = excelFieldList;
        this.readListeners = new HashMap<>(8);
        this.initResolver(excel, inputStream);
    }

    /**
     * Init excel read resolver
     *
     * @param excel       Excel annotation of Excel entity
     * @param inputStream File inputStream
     */
    private void initResolver(Excel excel, InputStream inputStream) {
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
        this.readerResolver = new ExcelReadExecutor<>(this.workbook, this.readListeners);
    }

    /**
     * Read excel
     *
     * @return this
     */
    public ExcelReader<R> read() {
        this.readerResolver.read(this.excelClass, 0, this.defaultSheetName,
                this.excelFieldList, this.collect);
        return this;
    }

    /**
     * Read the specified sheet
     *
     * @param sheetName sheet name
     * @return this
     */
    public ExcelReader<R> read(String sheetName) {
        this.readerResolver.read(this.excelClass, 0, sheetName,
                this.excelFieldList, this.collect);
        return this;
    }

    /**
     * Read the excel sheet
     *
     * @param startIndex Excel header starter index
     * @return this
     */
    public ExcelReader<R> read(int startIndex) {
        this.readerResolver.read(this.excelClass, startIndex, this.defaultSheetName, this.excelFieldList, this.collect);
        return this;
    }

    /**
     * Read the specified sheet
     *
     * @param startIndex Excel header starter index
     * @param sheetName  Excel Sheet name
     * @return this
     */
    public ExcelReader<R> read(int startIndex, String sheetName) {
        this.readerResolver.read(this.excelClass, startIndex, sheetName, this.excelFieldList, this.collect);
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
     * Add readListeners
     *
     * @param readListenerList Read listeners
     * @return this
     */
    public ExcelReader<R> addListener(List<ReadListener<R>> readListenerList) {
        readListenerList.forEach(this::addListener);
        return this;
    }

    /**
     * Subscribe to the results of the import
     *
     * @param readListener Read listener
     * @return this
     */
    @SuppressWarnings("all")
    public ExcelReader<R> addListener(ReadListener<R> readListener) {
        if (readListener instanceof RowReadListener) {
            List<ReadListener<R>> readListeners = this.readListeners.get(RowReadListener.class);
            if (readListeners == null) {
                readListeners = new ArrayList<>();
            }
            readListeners.add(readListener);
        }
        if (readListener instanceof EmptyReadListener) {
            List<ReadListener<R>> readListeners = this.readListeners.get(EmptyReadListener.class);
            if (readListeners == null) {
                readListeners = new ArrayList<>();
            }
            readListeners.add(readListener);
        }
        if (readListener instanceof ResultReadListener) {
            List<ReadListener<R>> readListeners = this.readListeners.get(ResultReadListener.class);
            if (readListeners == null) {
                readListeners = new ArrayList<>();
            }
            readListeners.add(readListener);
        }
        return this;
    }

    /**
     * Open the collection of Java objects generated for each row of the import
     *
     * @return this
     */
    public ExcelReader<R> enableCollect() {
        this.collect = true;
        return this;
    }

    /**
     * Close the collection of Java objects generated for each row of the import
     *
     * @return this
     */
    public ExcelReader<R> closeCollect() {
        this.collect = false;
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
