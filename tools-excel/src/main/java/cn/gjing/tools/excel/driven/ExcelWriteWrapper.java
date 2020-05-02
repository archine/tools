package cn.gjing.tools.excel.driven;

import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.listener.ExcelWriteListener;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Excel annotation-driven write wapper
 *
 * @author Gjing
 **/
@Getter
@ToString
public class ExcelWriteWrapper {
    private List<ExcelWriteListener> writeListeners;
    private BigTitle bigTitle;
    private List<?> data;
    private Map<String, String[]> boxValues;

    public ExcelWriteWrapper() {
    }

    public ExcelWriteWrapper(List<?> data) {
        this.data = data;
    }

    /**
     * Add write listener
     *
     * @param writeListener writeListener
     * @return this
     */
    public ExcelWriteWrapper addListener(ExcelWriteListener writeListener) {
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
    public ExcelWriteWrapper addListener(List<ExcelWriteListener> writeListeners) {
        if (this.writeListeners == null) {
            this.writeListeners = new ArrayList<>();
        }
        this.writeListeners.addAll(writeListeners);
        return this;
    }

    /**
     * Set excel big title
     *
     * @param bigTitle big title
     * @return this
     */
    public ExcelWriteWrapper title(BigTitle bigTitle) {
        this.bigTitle = bigTitle;
        return this;
    }

    /**
     * Set data
     *
     * @param data The data to be exported
     * @return this
     */
    public ExcelWriteWrapper data(List<?> data) {
        this.data = data;
        return this;
    }

    /**
     * Set dropdown box value
     *
     * @param boxValues box value map
     * @return this
     */
    public ExcelWriteWrapper boxValue(Map<String, String[]> boxValues) {
        this.boxValues = boxValues;
        return this;
    }
}
