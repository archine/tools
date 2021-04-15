package cn.gjing.tools.excel.metadata;

import cn.gjing.tools.excel.metadata.annotation.ListenerNative;
import cn.gjing.tools.excel.metadata.listener.ExcelListener;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gjing
 **/
public abstract class AbstractExcelContext {
    /**
     * Current workbook
     */
    @Getter
    @Setter
    protected Workbook workbook;

    /**
     * Current read sheet
     */
    @Getter
    @Setter
    protected Sheet sheet;

    /**
     * Listener cache
     */
    @Getter
    protected final List<ExcelListener> listenerCache;

    public AbstractExcelContext() {
        this.listenerCache = new ArrayList<>();
    }

    /**
     * Add listener instance to cache
     *
     * @param listener Excel listener
     */
    public void addListener(ExcelListener listener) {
        if (listener != null) {
            this.listenerCache.add(listener);
        }
    }

    /**
     * clear the current listener cache, save the listener marked with the @ListenerNative annotation
     *
     * @param all        Whether to delete all listeners. If false,
     *                   the listeners annotated by @ListenerNative will be retained
     * @param excelClass excelClass
     */
    protected void clearListener(boolean all, Class<?> excelClass) {
        if (all) {
            this.listenerCache.clear();
            return;
        }
        this.listenerCache.removeIf(e -> {
            ListenerNative listenerNative = e.getClass().getAnnotation(ListenerNative.class);
            if (listenerNative == null) {
                return true;
            }
            for (Class<?> aClass : listenerNative.value()) {
                if (excelClass == aClass) {
                    return true;
                }
            }
            return false;
        });
    }
}