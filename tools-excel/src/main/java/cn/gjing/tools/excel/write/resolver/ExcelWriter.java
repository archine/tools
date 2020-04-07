package cn.gjing.tools.excel.write.resolver;

import cn.gjing.tools.excel.BigTitle;
import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.metadata.CustomWrite;
import cn.gjing.tools.excel.metadata.ExcelWriterResolver;
import cn.gjing.tools.excel.util.BeanUtils;
import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.write.listener.*;
import cn.gjing.tools.excel.write.style.DefaultExcelStyle;
import lombok.Getter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;

/**
 * Excel writer
 *
 * @author Gjing
 **/
public class ExcelWriter {
    @Getter
    private String fileName;
    @Getter
    private Workbook workbook;
    private HttpServletResponse response;
    @Getter
    private List<Field> headFieldList;
    @Getter
    private List<String> headNames;
    @Getter
    private Excel excel;
    @Getter
    private String defaultSheetName = "sheet1";
    @Getter
    private Map<Class<? extends WriteListener>, List<WriteListener>> writeListenerMap;
    private ExcelWriterResolver writerResolver;

    private ExcelWriter() {

    }

    public ExcelWriter(String fileName, Excel excel, HttpServletResponse response, List<Field> headFieldList, boolean initDefaultStyle, List<String> headNames) {
        this.fileName = fileName;
        this.response = response;
        this.headFieldList = headFieldList;
        this.headNames = headNames;
        this.excel = excel;
        this.writeListenerMap = new HashMap<>(8);
        this.initResolver(excel);
        if (initDefaultStyle) {
            this.initStyle();
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
     * Init default style
     */
    private void initStyle() {
        List<WriteListener> cellWriteListeners = this.writeListenerMap.get(BaseCellWriteListener.class);
        if (cellWriteListeners == null) {
            cellWriteListeners = new ArrayList<>();
        }
        cellWriteListeners.add(new DefaultExcelStyle());
        this.writeListenerMap.put(BaseCellWriteListener.class, cellWriteListeners);
    }

    /**
     * To write
     *
     * @param data      data
     * @param headNames Excel head names
     * @return this
     */
    public ExcelWriter write(List<?> data, String... headNames) {
        return this.write(this.defaultSheetName, data, true, null, headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param sheetName sheet name
     * @param headNames Excel head names
     * @return this
     */
    public ExcelWriter write(String sheetName, List<?> data, String... headNames) {
        return this.write(sheetName, data, true, null, headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param headNames Excel head names
     * @param needHead  Whether need excel head
     * @return this
     */
    public ExcelWriter write(List<?> data, boolean needHead, String... headNames) {
        return this.write(this.defaultSheetName, data, needHead, null, headNames);
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
    public ExcelWriter write(String sheetName, List<?> data, boolean needHead, String... headNames) {
        return this.write(sheetName, data, needHead, null, headNames);
    }

    /**
     * To write
     *
     * @param data      data
     * @param boxValues dropdown box values
     * @param headNames Excel head names
     * @return this
     */
    public ExcelWriter write(List<?> data, Map<String, String[]> boxValues, String... headNames) {
        return this.write(this.defaultSheetName, data, true, boxValues, headNames);
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
    public ExcelWriter write(String sheetName, List<?> data, Map<String, String[]> boxValues, String... headNames) {
        return this.write(sheetName, data, true, boxValues, headNames);
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
    public ExcelWriter write(List<?> data, boolean needHead, Map<String, String[]> boxValues, String... headNames) {
        return this.write(this.defaultSheetName, data, needHead, boxValues, headNames);
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
    public ExcelWriter write(String sheetName, List<?> data, boolean needHead, Map<String, String[]> boxValues, String... headNames) {
        Sheet sheet = this.createSheet(sheetName);
        this.writerResolver.writeHead(this.headFieldList, headNames == null ? this.headNames : Arrays.asList(headNames), sheet, needHead, boxValues, excel)
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
        if (bigTitle != null && bigTitle.getLastCols() < 1) {
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
     * User defined export
     *
     * @param customWrite export logic
     * @return this
     */
    public ExcelWriter customWrite(CustomWrite customWrite) {
        this.writerResolver.customWrite(customWrite);
        return this;
    }

    /**
     * Add an write listener
     *
     * @param listener Write listener
     * @return this
     */
    public ExcelWriter addListener(WriteListener listener) {
        if (listener instanceof BaseSheetWriteListener) {
            List<WriteListener> listeners = this.writeListenerMap.get(BaseSheetWriteListener.class);
            if (listeners == null) {
                listeners = new ArrayList<>();
            }
            listeners.add(listener);
            return this;
        }
        if (listener instanceof BaseRowWriteListener) {
            List<WriteListener> listeners = this.writeListenerMap.get(BaseRowWriteListener.class);
            if (listeners == null) {
                listeners = new ArrayList<>();
            }
            listeners.add(listener);
            return this;
        }
        if (listener instanceof BaseCellWriteListener) {
            List<WriteListener> listeners = this.writeListenerMap.get(BaseCellWriteListener.class);
            if (listeners == null) {
                listeners = new ArrayList<>();
            }
            listeners.add(listener);
        }
        if (listener instanceof BaseMergeWriteListener) {
            List<WriteListener> listeners = this.writeListenerMap.get(BaseCellWriteListener.class);
            if (listeners == null) {
                listeners = new ArrayList<>();
            }
            listeners.add(listener);
        }
        return this;
    }

    /**
     * Add multiple write listener
     *
     * @param listeners Write listener list
     * @return this
     */
    public ExcelWriter addListener(List<? extends WriteListener> listeners) {
        listeners.forEach(this::addListener);
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
        this.excel = excel;
        if (resetListener) {
            this.writeListenerMap.clear();
        }
        return this;
    }

    /**
     * Output the contents to excel of the cache
     */
    public void flush() {
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
            if (this.excel.lock()) {
                sheet.protectSheet(this.excel.secret());
            }
        }
        return sheet;
    }
}
