package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.read.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.listener.ExcelResultReadListener;
import cn.gjing.tools.excel.util.ListenerChain;
import lombok.Data;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

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
@Data
public class ExcelReaderContext<R> {
    /**
     * Current workbook
     */
    private Workbook workbook;

    /**
     * Current read sheet
     */
    private Sheet sheet;

    /**
     * Header data for an Excel file
     */
    private List<String> headNames;

    /**
     * Current excel mapping entity
     */
    private Class<R> excelClass;

    /**
     * Excel header mapping field
     */
    private Map<String, Field> excelFieldMap;

    /**
     * Check that the Excel file is bound to the currently set mapping entity
     */
    private boolean checkTemplate = false;

    /**
     * Read rows before the header
     */
    private boolean headBefore = false;

    /**
     * The unique key
     */
    private String uniqueKey;

    /**
     * Ignore the array of actual Excel table headers that you read when importing
     */
    private String[] ignores;

    /**
     * Read listener cache
     */
    private Map<Class<? extends ExcelReadListener>, List<ExcelReadListener>> readListenersCache = new HashMap<>(8);

    /**
     * Read result listener
     */
    private ExcelResultReadListener<R> resultReadListener;

    public ExcelReaderContext(Class<R> excelClass, Map<String, Field> excelFieldMap, String[] ignores) {
        this.excelClass = excelClass;
        this.excelFieldMap = excelFieldMap;
        this.headNames = new ArrayList<>();
        this.ignores = ignores;
    }

    public void addListener(ExcelReadListener readListener) {
        ListenerChain.addReadListener(this.readListenersCache, readListener);
    }
}
