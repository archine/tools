package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.read.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.listener.ExcelResultReadListener;
import cn.gjing.tools.excel.util.ListenerChain;
import lombok.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
     * Real header
     */
    @Builder.Default
    private List<String> headNames = new ArrayList<>();

    /**
     * Current excel header mapping field
     */
    private List<Field> excelFields;

    /**
     * Whether to check excel template when excel import
     */
    private boolean templateCheck = true;

    /**
     * Read listener cache
     */
    @Builder.Default
    private Map<Class<? extends ExcelReadListener>, List<ExcelReadListener>> readListenersCache = new HashMap<>(8);

    private ExcelResultReadListener<R> resultReadListener;

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
