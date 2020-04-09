package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.read.listener.ExcelReadListener;
import cn.gjing.tools.excel.util.ListenerChain;
import lombok.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gjing
 **/
@Getter
public class ExcelReaderContext<R> {
    /**
     * Current workbook
     */
    @Setter
    private Workbook workbook;
    /**
     * Current sheet
     */
    @Setter
    private Sheet sheet;
    /**
     * File inputStream
     */
    @Setter
    private InputStream inputStream;
    /**
     * Whether to turn on collection mode
     */
    @Setter
    private Boolean collectMode;

    /**
     * Current excel mapping entity
     */
    @Setter
    private Class<R> excelClass;

    /**
     * Real header
     */
    @Setter
    private List<String> headNames;

    /**
     * Current excel header mapping field
     */
    @Setter
    private List<Field> excelFields;

    /**
     * Read listener cache
     */
    @Builder.Default
    private Map<Class<? extends ExcelReadListener>, List<ExcelReadListener>> readListenersCache = new HashMap<>(8);

    public ExcelReaderContext(InputStream inputStream, Class<R> excelClass, List<Field> excelFields) {
        this.inputStream = inputStream;
        this.excelClass = excelClass;
        this.excelFields = excelFields;
    }

    public ExcelReaderContext<R> addListener(ExcelReadListener readListener) {
        ListenerChain.addReadListener(this.readListenersCache, readListener);
        return this;
    }
}
