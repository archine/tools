package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.metadata.ExcelWriterResolver;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ExcelUtils;
import cn.gjing.tools.excel.util.ListenerUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.listener.*;
import cn.gjing.tools.excel.write.style.DefaultExcelStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Excel writer
 *
 * @author Gjing
 **/
public class ExcelWriter {
    private String fileName;
    private Workbook workbook;
    private HttpServletResponse response;
    private List<Field> headFieldList;
    private List<String[]> headNames;
    private String defaultSheetName = "sheet1";
    private Map<Class<? extends WriteListener>, List<WriteListener>> writeListenerMap;
    private boolean needValid;
    private boolean isMultiHead;
    private ExcelWriterResolver writerResolver;

    private ExcelWriter() {

    }

    public ExcelWriter(String fileName, Excel excel, HttpServletResponse response, List<Field> headFieldList, boolean initDefaultStyle, List<String[]> headNames) {
        this.fileName = fileName;
        this.response = response;
        this.headFieldList = headFieldList;
        this.headNames = headNames;
        this.writeListenerMap = new HashMap<>(8);
        this.initResolver(excel);
        if (initDefaultStyle) {
            this.initStyle(this.workbook);
        }
    }

    /**
     * Init resolver
     *
     * @param excel excel
     */
    private void initResolver(Excel excel) {
        switch (excel.type()) {
            case XLS:
                this.workbook = new HSSFWorkbook();
                this.writerResolver = new ExcelWriteXlsResolver((HSSFWorkbook) workbook, this.writeListenerMap);
                break;
            case XLSX:
                this.workbook = new SXSSFWorkbook(excel.windowSize());
                this.writerResolver = new ExcelWriteXlsxResolver((SXSSFWorkbook) workbook, this.writeListenerMap);
                break;
            default:
                throw new ExcelInitException("No corresponding processor was found");
        }
    }

    /**
     * Init default style listener
     */
    private void initStyle(Workbook workbook) {
        DefaultExcelStyle defaultExcelStyle = new DefaultExcelStyle();
        defaultExcelStyle.init(workbook);
        ExcelUtils.addWriteListener(this.writeListenerMap, defaultExcelStyle);
    }

    /**
     * To write
     *
     * @param data data
     * @return this
     */
    public ExcelWriter write(List<?> data) {
        return this.write(data, this.defaultSheetName, true, null, this.headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param headNames Excel head names
     * @return this
     */
    public ExcelWriter write(List<?> data, List<String[]> headNames) {
        return this.write(data, this.defaultSheetName, true, null, headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName) {
        return this.write(data, sheetName, true, null, this.headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @param headNames Excel head names
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, List<String[]> headNames) {
        return this.write(data, sheetName, true, null, headNames);
    }

    /**
     * To write
     *
     * @param data     data
     * @param needHead Whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, boolean needHead) {
        return this.write(data, this.defaultSheetName, needHead, null, this.headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param headNames Excel head names
     * @param needHead  Whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, boolean needHead, List<String[]> headNames) {
        return this.write(data, this.defaultSheetName, needHead, null, headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @param needHead  Whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, boolean needHead) {
        return this.write(data, sheetName, needHead, null, this.headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @param headNames Excel head names
     * @param needHead  Whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, boolean needHead, List<String[]> headNames) {
        return this.write(data, sheetName, needHead, null, headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param boxValues dropdown box values
     * @return this
     */
    public ExcelWriter write(List<?> data, Map<String, String[]> boxValues) {
        return this.write(data, this.defaultSheetName, true, boxValues, this.headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param boxValues dropdown box values
     * @param headNames Excel head names
     * @return this
     */
    public ExcelWriter write(List<?> data, Map<String, String[]> boxValues, List<String[]> headNames) {
        return this.write(data, this.defaultSheetName, true, boxValues, headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @param boxValues dropdown box values
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Map<String, String[]> boxValues) {
        return this.write(data, sheetName, true, boxValues, this.headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @param boxValues dropdown box values
     * @param headNames Excel head names
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, Map<String, String[]> boxValues, List<String[]> headNames) {
        return this.write(data, sheetName, true, boxValues, headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param boxValues dropdown box values
     * @param needHead  Whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, boolean needHead, Map<String, String[]> boxValues) {
        return this.write(data, this.defaultSheetName, needHead, boxValues, this.headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param boxValues dropdown box values
     * @param headNames Excel head names
     * @param needHead  Whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, boolean needHead, Map<String, String[]> boxValues, List<String[]> headNames) {
        return this.write(data, this.defaultSheetName, needHead, boxValues, headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @param boxValues dropdown box values
     * @param headNames Excel head names
     * @param needHead  Whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, String sheetName, boolean needHead, Map<String, String[]> boxValues, List<String[]> headNames) {
        Sheet sheet = this.createSheet(sheetName);
        this.writerResolver.writeHead(this.headFieldList, headNames == null ? this.headNames : headNames, sheet, needHead, boxValues,
                this.needValid, this.isMultiHead)
                .write(data, sheet, this.headFieldList);
        return this;
    }

    /**
     * Write excel big title
     * This method only fires the style listener
     *
     * @param bigTitle Big title
     * @return this
     */
    public ExcelWriter writeTitle(BigTitle bigTitle) {
        return this.writeTitle(bigTitle, this.defaultSheetName);
    }

    /**
     * Write excel big title
     * This method only fires the style listener
     *
     * @param bigTitle  Big title
     * @param sheetName Sheet name
     * @return this
     */
    public ExcelWriter writeTitle(BigTitle bigTitle, String sheetName) {
        if (bigTitle.getLastCols() < 1) {
            bigTitle.setLastCols(this.headFieldList.size());
        }
        this.writerResolver.writeTitle(bigTitle, this.createSheet(sheetName));
        return this;
    }

    /**
     * Reset the processor before write operation
     *
     * @param excelWriteResolver Excel write Resolver
     * @return this
     */
    public ExcelWriter resetResolver(Supplier<? extends ExcelWriterResolver> excelWriteResolver) {
        this.writerResolver = excelWriteResolver.get();
        return this;
    }

    /**
     * Whether enable excel valid
     *
     * @return this
     */
    public ExcelWriter enableValid() {
        this.needValid = true;
        return this;
    }

    /**
     * Whether enable excel valid
     *
     * @return this
     */
    public ExcelWriter closeValid() {
        this.needValid = false;
        return this;
    }

    /**
     * Whether close multi excel head
     *
     * @return this
     */
    public ExcelWriter enableMultiHead() {
        this.isMultiHead = true;
        return this;
    }

    /**
     * Whether close multi excel head
     *
     * @return this
     */
    public ExcelWriter closeMultiHead() {
        this.isMultiHead = false;
        return this;
    }

    /**
     * Add an write listener
     *
     * @param listener Write listener
     * @return this
     */
    public ExcelWriter addListener(WriteListener listener) {
        ExcelUtils.addWriteListener(this.writeListenerMap, listener);
        return this;
    }

    /**
     * Add multiple write listener
     *
     * @param listeners Write listener list
     * @return this
     */
    public ExcelWriter addListener(List<? extends WriteListener> listeners) {
        listeners.forEach(e -> ExcelUtils.addWriteListener(this.writeListenerMap, e));
        return this;
    }

    /**
     * Reset Excel mapped entity
     *
     * @param excelClass    Excel mapped entity
     * @param ignores       The exported field is to be ignored
     * @param resetListener Whether to reset the listener
     * @return this
     */
    public ExcelWriter resetExcelClass(Class<?> excelClass, boolean resetListener, String... ignores) {
        Excel excel = excelClass.getAnnotation(Excel.class);
        ParamUtils.requireNonNull(excel, "Failed to reset Excel class, the @Excel annotation was not found on the " + excelClass);
        this.headNames = new ArrayList<>();
        this.headFieldList = BeanUtils.getExcelFields(excelClass, ignores, headNames);
        if (resetListener) {
            this.writeListenerMap.clear();
        }
        return this;
    }

    /**
     * Flush all content to excel of the cache
     */
    public void flush() {
        ListenerUtils.workbookFlushBefore(this.writeListenerMap, this.workbook, this.fileName);
        this.writerResolver.flush(this.response, this.fileName);
        if (this.workbook instanceof SXSSFWorkbook) {
            ((SXSSFWorkbook) this.workbook).dispose();
        }
    }

    /**
     * Create excel sheet
     *
     * @param sheetName sheet name
     */
    private Sheet createSheet(String sheetName) {
        Sheet sheet = this.workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = this.workbook.createSheet(sheetName);
            ListenerUtils.completeSheet(this.writeListenerMap, sheet);
        }
        return sheet;
    }
}
