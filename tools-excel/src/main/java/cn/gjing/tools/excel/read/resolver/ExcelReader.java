package cn.gjing.tools.excel.read.resolver;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.metadata.ExcelReaderResolver;
import cn.gjing.tools.excel.read.ExcelReaderContext;
import cn.gjing.tools.excel.read.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.listener.ExcelResultReadListener;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

/**
 * Excel reader
 *
 * @author Gjing
 **/
public class ExcelReader<R> {
    private ExcelReaderContext<R> context;
    private ExcelReaderResolver<R> readerResolver;
    private String defaultSheetName = "sheet1";

    private ExcelReader() {

    }

    public ExcelReader(ExcelReaderContext<R> context, Excel excel) {
        this.context = context;
        this.initResolver(excel);
    }

    /**
     * Init excel read resolver
     *
     * @param excel Excel annotation of Excel entity
     */
    private void initResolver(Excel excel) {
        switch (excel.type()) {
            case XLS:
                try {
                    this.context.setWorkbook(new HSSFWorkbook(this.context.getInputStream()));
                } catch (IOException e) {
                    throw new ExcelInitException("Init workbook error, " + e.getMessage());
                }
                break;
            case XLSX:
                Workbook workbook = StreamingReader.builder()
                        .rowCacheSize(excel.cacheRowSize())
                        .bufferSize(excel.bufferSize())
                        .open(this.context.getInputStream());
                this.context.setWorkbook(workbook);
                break;
            default:
                throw new ExcelInitException("No corresponding processor was found");
        }
        this.readerResolver = new ReadExecutor<>(this.context);
    }

    /**
     * Read excel
     *
     * @return this
     */
    public ExcelReader<R> read() {
        this.readerResolver.read(0, this.defaultSheetName);
        return this;
    }

    /**
     * Read the specified sheet
     *
     * @param sheetName sheet name
     * @return this
     */
    public ExcelReader<R> read(String sheetName) {
        this.readerResolver.read(0, sheetName);
        return this;
    }

    /**
     * Read the excel sheet
     *
     * @param startIndex Excel header starter index
     * @return this
     */
    public ExcelReader<R> read(int startIndex) {
        this.readerResolver.read(startIndex, this.defaultSheetName);
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
        this.readerResolver.read(startIndex, sheetName);
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
     * Add readListenersMap
     *
     * @param readListenerList Read listeners
     * @return this
     */
    public ExcelReader<R> addListener(List<ExcelReadListener> readListenerList) {
        readListenerList.forEach(this.context::addListener);
        this.context.setCollectMode(false);
        return this;
    }

    /**
     * Subscribe to the results of the import
     *
     * @param readListener Read listener
     * @return this
     */
    public ExcelReader<R> addListener(ExcelReadListener readListener) {
        this.context.addListener(readListener);
        this.context.setCollectMode(false);
        return this;
    }

    /**
     * Subscribe to the data after the import is complete
     *
     * @param excelResultReadListener resultReadListener
     * @return this
     */
    public ExcelReader<R> subscribe(ExcelResultReadListener<R> excelResultReadListener) {
        this.context.addListener(excelResultReadListener);
        this.context.setCollectMode(true);
        return this;
    }

    /**
     * Excel import end
     */
    public void end() {
        try {
            if (this.context.getInputStream() != null) {
                this.context.getInputStream().close();
            }
            if (this.context.getWorkbook() != null) {
                this.context.getWorkbook().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
