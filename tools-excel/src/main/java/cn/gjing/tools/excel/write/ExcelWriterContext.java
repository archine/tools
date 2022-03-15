package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.metadata.AbstractExcelContext;
import cn.gjing.tools.excel.metadata.ExcelFieldProperty;
import cn.gjing.tools.excel.metadata.ExcelType;

import java.util.List;

/**
 * Excel writer global context
 *
 * @author Gjing
 **/
public final class ExcelWriterContext extends AbstractExcelContext {
    /**
     * Current excel mapping entity
     */
    private Class<?> excelClass;

    /**
     * Excel file name
     */
    private String fileName;

    /**
     * Whether validation annotations are enabled
     */
    private boolean needValid = false;

    /**
     * Whether to open multistage Excel headers
     */
    private boolean multiHead = false;

    /**
     * Whether a header exists
     */
    private boolean existHead = true;

    /**
     * Whether is excel template file
     */
    private boolean isTemplate = false;

    /**
     * Whether you need to add a file identifier when exporting an Excel file,
     * which can be used for template validation of the file at import time
     */
    private boolean bind = true;

    /**
     * The unique key
     */
    private String uniqueKey;

    /**
     * Excel type
     */
    private ExcelType excelType = ExcelType.XLS;

    /**
     * Excel head row height
     */
    private short headerHeight = 450;

    /**
     * Excel body row height
     */
    private short bodyHeight = 390;

    /**
     * Excel header series
     */
    private int headerSeries = 1;

    /**
     * Excel filed property
     */
    private List<ExcelFieldProperty> fieldProperties;

    public ExcelWriterContext() {
        super();
    }

    public Class<?> getExcelClass() {
        return excelClass;
    }

    public void setExcelClass(Class<?> excelClass) {
        this.excelClass = excelClass;
    }

    public void setExcelClass(Class<?> excelClass, boolean all) {
        if (this.excelClass != null) {
            super.clearListener(all, excelClass);
        }
        this.excelClass = excelClass;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isNeedValid() {
        return needValid;
    }

    public void setNeedValid(boolean needValid) {
        this.needValid = needValid;
    }

    public boolean isMultiHead() {
        return multiHead;
    }

    public void setMultiHead(boolean multiHead) {
        this.multiHead = multiHead;
    }

    public boolean isExistHead() {
        return existHead;
    }

    public void setExistHead(boolean existHead) {
        this.existHead = existHead;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean template) {
        isTemplate = template;
    }

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public ExcelType getExcelType() {
        return excelType;
    }

    public void setExcelType(ExcelType excelType) {
        this.excelType = excelType;
    }

    public short getHeaderHeight() {
        return headerHeight;
    }

    public void setHeaderHeight(short headerHeight) {
        this.headerHeight = headerHeight;
    }

    public short getBodyHeight() {
        return bodyHeight;
    }

    public void setBodyHeight(short bodyHeight) {
        this.bodyHeight = bodyHeight;
    }

    public int getHeaderSeries() {
        return headerSeries;
    }

    public void setHeaderSeries(int headerSeries) {
        this.headerSeries = headerSeries;
    }

    public List<ExcelFieldProperty> getFieldProperties() {
        return fieldProperties;
    }

    public void setFieldProperties(List<ExcelFieldProperty> fieldProperties) {
        this.fieldProperties = fieldProperties;
    }
}
