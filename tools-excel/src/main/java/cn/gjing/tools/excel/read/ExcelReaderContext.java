package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.read.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.listener.ExcelResultReadListener;
import cn.gjing.tools.excel.util.ListenerChain;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel reader global context
 *
 * @author Gjing
 **/
@Getter
@Setter
public class ExcelReaderContext<R> {
    /**
     * Current workbook
     */
    private Workbook workbook;

    /**
     * Current sheet
     */
    private Sheet sheet;

    /**
     * File inputStream
     */
    private InputStream inputStream;

    /**
     * Current excel mapping entity
     */
    private Class<R> excelClass;

    /**
     * Current excel header mapping field
     */
    private List<Field> excelFields;

    /**
     * Check that the Excel file is bound to the currently set mapping entity
     */
    private boolean checkTemplate = false;

    /**
     * Whether is need meta info(Such as header,title)
     */
    private boolean metaInfo = false;

    /**
     * Read listener cache
     */
    private Map<Class<? extends ExcelReadListener>, List<ExcelReadListener>> readListenersCache = new HashMap<>(8);

    private ExcelResultReadListener<R> resultReadListener;

    public ExcelReaderContext(InputStream inputStream, Class<R> excelClass, List<Field> excelFields) {
        this.inputStream = inputStream;
        this.excelClass = excelClass;
        this.excelFields = excelFields;
    }

    public void addListener(ExcelReadListener readListener) {
        ListenerChain.addReadListener(this.readListenersCache, readListener);
    }
}
