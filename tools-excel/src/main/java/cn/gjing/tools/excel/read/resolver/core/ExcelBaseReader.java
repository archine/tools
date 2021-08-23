package cn.gjing.tools.excel.read.resolver.core;

import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.exception.ExcelTemplateException;
import cn.gjing.tools.excel.metadata.ExcelType;
import cn.gjing.tools.excel.metadata.ExecType;
import cn.gjing.tools.excel.metadata.aware.ExcelReaderContextAware;
import cn.gjing.tools.excel.metadata.aware.ExcelWorkbookAware;
import cn.gjing.tools.excel.metadata.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.ExcelReaderContext;
import cn.gjing.tools.excel.read.listener.ExcelResultReadListener;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Excel base reader
 *
 * @author Gjing
 **/
public abstract class ExcelBaseReader<R> {
    protected ExcelReaderContext<R> context;
    protected InputStream inputStream;
    protected ExcelBaseReadExecutor<R> baseReadExecutor;
    protected final String defaultSheetName = "Sheet1";

    public ExcelBaseReader(ExcelReaderContext<R> context, InputStream inputStream, ExcelType excelType, int cacheRowSize, int bufferSize, ExecType execType) {
        this.context = context;
        this.inputStream = inputStream;
        this.chooseResolver(excelType, cacheRowSize, bufferSize, execType);
    }

    /**
     * Release resources after the read is complete
     */
    public void finish() {
        try {
            if (this.inputStream != null) {
                this.inputStream.close();
            }
            if (this.context.getWorkbook() != null) {
                this.context.getWorkbook().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add readListeners
     *
     * @param readListenerList Read listeners
     */
    protected void addListenerToCache(List<? extends ExcelReadListener> readListenerList) {
        if (readListenerList != null) {
            readListenerList.forEach(this::addListenerToCache);
        }
    }

    /**
     * Add readListeners
     *
     * @param readListener Read listener
     */
    @SuppressWarnings("unchecked")
    protected void addListenerToCache(ExcelReadListener readListener) {
        this.context.addListener(readListener);
        if (readListener instanceof ExcelReaderContextAware) {
            ((ExcelReaderContextAware<R>) readListener).setContext(this.context);
        }
        if (readListener instanceof ExcelWorkbookAware) {
            ((ExcelWorkbookAware) readListener).setWorkbook(this.context.getWorkbook());
        }
    }

    /**
     * Subscribe to the data after the import is complete
     *
     * @param excelResultReadListener resultReadListener
     */
    protected void addSubscribe(ExcelResultReadListener<R> excelResultReadListener) {
        this.context.setResultReadListener(excelResultReadListener);
    }

    private void chooseResolver(ExcelType excelType, int cacheRowSize, int bufferSize, ExecType execType) {
        switch (excelType) {
            case XLS:
                try {
                    this.context.setWorkbook(new HSSFWorkbook(this.inputStream));
                } catch (NotOLE2FileException | OfficeXmlFileException exception) {
                    exception.printStackTrace();
                    throw new ExcelTemplateException();
                } catch (IOException e) {
                    throw new ExcelInitException("Init workbook error, " + e.getMessage());
                }
                break;
            case XLSX:
                Workbook workbook;
                try {
                    workbook = StreamingReader.builder()
                            .rowCacheSize(cacheRowSize)
                            .bufferSize(bufferSize)
                            .open(this.inputStream);
                } catch (NotOfficeXmlFileException e) {
                    e.printStackTrace();
                    throw new ExcelTemplateException();
                }
                this.context.setWorkbook(workbook);
                break;
            default:
                throw new ExcelInitException("Excel type cannot be null");
        }
        this.baseReadExecutor = execType == ExecType.BIND ? new ExcelBindReadExecutor<>(this.context) : new ExcelSimpleReadExecutor<>(this.context);
    }

}
