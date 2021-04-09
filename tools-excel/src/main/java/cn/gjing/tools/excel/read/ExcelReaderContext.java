package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.metadata.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.listener.ExcelResultReadListener;
import lombok.Getter;
import lombok.Setter;
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
public class ExcelReaderContext<R> {
    /**
     * Current workbook
     */
    @Getter
    @Setter
    private Workbook workbook;

    /**
     * Current read sheet
     */
    @Getter
    @Setter
    private Sheet sheet;

    /**
     * Header data for an Excel file
     */
    @Getter
    @Setter
    private List<String> headNames;

    /**
     * Current excel mapping entity
     */
    @Getter
    @Setter
    private Class<R> excelClass;

    /**
     * Excel header mapping field
     */
    @Getter
    @Setter
    private Map<String, Field> excelFieldMap;

    /**
     * Check that the Excel file is bound to the currently set mapping entity
     */
    @Getter
    @Setter
    private boolean checkTemplate = false;

    /**
     * Read rows before the header
     */
    @Getter
    @Setter
    private boolean headBefore = false;

    /**
     * The unique key
     */
    @Getter
    @Setter
    private String uniqueKey;

    /**
     * Ignore the array of actual Excel table headers that you read when importing
     */
    @Getter
    @Setter
    private String[] ignores;

    /**
     * Read listener cache
     */
    @Getter
    private final Map<Class<? extends ExcelReadListener>, List<ExcelReadListener>> readListenersCache = new HashMap<>(8);

    /**
     * Read result listener
     */
    @Getter
    @Setter
    private ExcelResultReadListener<R> resultReadListener;

    public ExcelReaderContext(Class<R> excelClass, Map<String, Field> excelFieldMap, String[] ignores) {
        this.excelClass = excelClass;
        this.excelFieldMap = excelFieldMap;
        this.headNames = new ArrayList<>();
        this.ignores = ignores;
    }
}
