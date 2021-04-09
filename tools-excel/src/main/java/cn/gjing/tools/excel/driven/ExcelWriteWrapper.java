package cn.gjing.tools.excel.driven;

import cn.gjing.tools.excel.metadata.listener.ExcelWriteListener;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.valid.ExcelDropdownBox;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Excel annotation-driven write wrapper
 *
 * @author Gjing
 **/
@Getter
public class ExcelWriteWrapper {
    private List<ExcelWriteListener> writeListeners;
    private Map<String, String[]> boxValues;
    private String[] ignores;
    private String fileName;
    private final List<Object> dataList;

    private ExcelWriteWrapper() {
        this.dataList = new ArrayList<>();
    }

    /**
     * Build a write wrapper
     *
     * @return ExcelWriteWrapper
     */
    public static ExcelWriteWrapper build() {
        return new ExcelWriteWrapper();
    }

    /**
     * Build a write wrapper
     *
     * @param data The data to be exported
     * @return ExcelWriteWrapper
     */
    public static ExcelWriteWrapper build(List<?> data) {
        return new ExcelWriteWrapper().data(data);
    }

    /**
     * Build a write wrapper
     *
     * @param data     The data to be exported
     * @param fileName Excel file name
     * @return ExcelWriteWrapper
     */
    public static ExcelWriteWrapper build(String fileName, List<?> data) {
        return new ExcelWriteWrapper().fileName(fileName).data(data);
    }

    /**
     * Build a write wrapper
     *
     * @param fileName Excel file name
     * @return ExcelWriteWrapper
     */
    public static ExcelWriteWrapper build(String fileName) {
        return new ExcelWriteWrapper().fileName(fileName);
    }

    /**
     * Add write listener
     *
     * @param writeListener writeListener
     * @return this
     */
    public ExcelWriteWrapper listener(ExcelWriteListener writeListener) {
        if (this.writeListeners == null) {
            this.writeListeners = new ArrayList<>();
        }
        this.writeListeners.add(writeListener);
        return this;
    }

    /**
     * Add write listener
     *
     * @param writeListeners writeListener
     * @return this
     */
    public ExcelWriteWrapper listener(List<ExcelWriteListener> writeListeners) {
        if (this.writeListeners == null) {
            this.writeListeners = new ArrayList<>();
        }
        this.writeListeners.addAll(writeListeners);
        return this;
    }

    /**
     * Set excel big title.{@link BigTitle}
     *
     * @param bigTitle big title
     * @return this
     */
    public ExcelWriteWrapper title(BigTitle bigTitle) {
        if (bigTitle != null) {
            this.dataList.add(bigTitle);
        }
        return this;
    }

    /**
     * Set data
     *
     * @param data The data to be exported
     * @return this
     */
    public ExcelWriteWrapper data(List<?> data) {
        this.dataList.add(data == null ? new ArrayList<>() : data);
        return this;
    }

    /**
     * Set dropdown box value.{@link ExcelDropdownBox}
     *
     * @param boxValues box value map
     * @return this
     */
    public ExcelWriteWrapper boxValue(Map<String, String[]> boxValues) {
        this.boxValues = boxValues;
        return this;
    }

    /**
     * Which table heads to be ignored when exporting
     *
     * @param ignores ignore heads
     * @return this
     */
    public ExcelWriteWrapper ignores(String... ignores) {
        this.ignores = ignores;
        return this;
    }

    /**
     * Set excel file name
     *
     * @param fileName Excel file name
     * @return this
     */
    public ExcelWriteWrapper fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }
}
