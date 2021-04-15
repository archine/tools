package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.metadata.AbstractExcelContext;
import cn.gjing.tools.excel.metadata.ExcelType;
import cn.gjing.tools.excel.write.valid.handle.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
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
     * Valid annotation handler
     */
    private List<ExcelValidAnnotationHandler> validAnnotationHandlers;

    public ExcelWriterContext() {
        super();
    }

    public ExcelWriterContext(Class<?> excelClass, String fileName, boolean needValid, boolean multiHead, List<Field> excelFields, List<String[]> headNames,
                              boolean bind, String uniqueKey, ExcelType excelType, short headerHeight, short bodyHeight, int headerSeries,
                              List<ExcelValidAnnotationHandler> validAnnotationHandlers) {
        this.excelClass = excelClass;
        this.fileName = fileName;
        this.needValid = needValid;
        this.multiHead = multiHead;
        this.excelFields = excelFields;
        this.headNames = headNames;
        this.bind = bind;
        this.uniqueKey = uniqueKey;
        this.excelType = excelType;
        this.headerHeight = headerHeight;
        this.bodyHeight = bodyHeight;
        this.headerSeries = headerSeries;
        this.validAnnotationHandlers = validAnnotationHandlers;
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
            this.validAnnotationHandlers = Arrays.asList(
                    new CustomValidHandler(),
                    new DateValidHandler(),
                    new DropdownBoxValidHandler(),
                    new NumericValidHandler(),
                    new RepeatValidHandler());
        }
    }

    public boolean isMultiHead() {
        return multiHead;
    }

    public void setMultiHead(boolean multiHead) {
        this.multiHead = multiHead;
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

    public List<ExcelValidAnnotationHandler> getValidAnnotationHandlers() {
        return validAnnotationHandlers;
    }
}
