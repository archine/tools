package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.metadata.AbstractExcelContext;
import cn.gjing.tools.excel.metadata.ExcelType;
import cn.gjing.tools.excel.write.resolver.simple.ExcelSimpleWriterPropSupplier;
import cn.gjing.tools.excel.write.valid.handle.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
     * Excel header fields
     */
    private List<Field> excelFields = new ArrayList<>();

    /**
     * Excel header names
     */
    private List<String[]> headNames = new ArrayList<>();

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
    private short headerHeight = 400;

    /**
     * Excel body row height
     */
    private short bodyHeight = 370;

    /**
     * Excel header series
     */
    private int headerSeries = 1;

    /**
     * Simple type export supplier
     */
    private ExcelSimpleWriterPropSupplier propSupplier;

    /**
     * Valid annotation handler
     */
    private List<ExcelValidAnnotationHandler> validAnnotationHandlers;

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
        if (needValid) {
            this.validAnnotationHandlers = new ArrayList<>();
            this.validAnnotationHandlers.add(new CustomValidHandler());
            this.validAnnotationHandlers.add(new DateValidHandler());
            this.validAnnotationHandlers.add(new DropdownBoxValidHandler());
            this.validAnnotationHandlers.add(new NumericValidHandler());
            this.validAnnotationHandlers.add(new RepeatValidHandler());
        }
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

    public List<Field> getExcelFields() {
        return excelFields;
    }

    public void setExcelFields(List<Field> excelFields) {
        this.excelFields = excelFields;
    }

    public List<String[]> getHeadNames() {
        return headNames;
    }

    public void setHeadNames(List<String[]> headNames) {
        this.headNames = headNames;
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

    public ExcelSimpleWriterPropSupplier getPropSupplier() {
        return propSupplier;
    }

    public void setPropSupplier(ExcelSimpleWriterPropSupplier propSupplier) {
        this.propSupplier = propSupplier;
    }

    public List<ExcelValidAnnotationHandler> getValidAnnotationHandlers() {
        return validAnnotationHandlers;
    }
}
