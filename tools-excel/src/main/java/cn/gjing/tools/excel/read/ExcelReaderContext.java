package cn.gjing.tools.excel.read;

import cn.gjing.tools.excel.metadata.AbstractExcelContext;
import cn.gjing.tools.excel.read.listener.ExcelResultReadListener;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Excel reader global context
 *
 * @author Gjing
 **/
@Getter
@Setter
public class ExcelReaderContext<R> extends AbstractExcelContext {
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
     * Read result listener
     */
    private ExcelResultReadListener<R> resultReadListener;

    public ExcelReaderContext() {
    }

    public ExcelReaderContext(Class<R> excelClass, Map<String, Field> excelFieldMap, String[] ignores) {
        this.excelClass = excelClass;
        this.excelFieldMap = excelFieldMap;
        this.headNames = new ArrayList<>();
        this.ignores = ignores;
        this.checkTemplate = false;
        this.headBefore = false;
    }

    public ExcelReaderContext(List<String> headNames, Class<R> excelClass, Map<String, Field> excelFieldMap, boolean checkTemplate,
                              boolean headBefore, String uniqueKey, String[] ignores,
                              ExcelResultReadListener<R> resultReadListener) {
        this.headNames = headNames;
        this.excelClass = excelClass;
        this.excelFieldMap = excelFieldMap;
        this.checkTemplate = checkTemplate;
        this.headBefore = headBefore;
        this.uniqueKey = uniqueKey;
        this.ignores = ignores;
        this.resultReadListener = resultReadListener;
    }
}
