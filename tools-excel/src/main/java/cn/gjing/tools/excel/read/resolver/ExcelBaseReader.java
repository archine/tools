package cn.gjing.tools.excel.read.resolver;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.exception.ExcelTemplateException;
import cn.gjing.tools.excel.metadata.resolver.ExcelReaderResolver;
import cn.gjing.tools.excel.read.ExcelReaderContext;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;

/**
 * Excel base writer
 *
 * @author Gjing
 **/
public abstract class ExcelBaseReader<R> {
    protected ExcelReaderContext<R> context;
    protected InputStream inputStream;
    protected ExcelReaderResolver<R> readerResolver;
    protected final String defaultSheetName = "Sheet1";

    public ExcelBaseReader(ExcelReaderContext<R> context, InputStream inputStream, Excel excel) {
        this.context = context;
        this.inputStream = inputStream;
        this.chooseResolver(excel);
    }

    /**
     * The Excel read data end
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
     * Choose excel read resolver
     *
     * @param excel Excel annotation of Excel entity
     */
    private void chooseResolver(Excel excel) {
        switch (excel.type()) {
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
                            .rowCacheSize(excel.cacheRowSize())
                            .bufferSize(excel.bufferSize())
                            .open(this.inputStream);
                } catch (NotOfficeXmlFileException e) {
                    e.printStackTrace();
                    throw new ExcelTemplateException();
                }
                this.context.setWorkbook(workbook);
                break;
            default:
        }
        this.readerResolver = new ExcelReadExecutor<>();
        this.readerResolver.init(this.context);
    }
}
