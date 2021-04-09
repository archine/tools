package cn.gjing.tools.excel.read.listener;

import cn.gjing.tools.excel.metadata.listener.ExcelReadListener;
import cn.gjing.tools.excel.read.resolver.ExcelBindReader;

import java.util.List;

/**
 * Import completion result listener. If not set, no data will be collected.
 * You can set it by {@link ExcelBindReader#subscribe(ExcelResultReadListener)}
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelResultReadListener<R> extends ExcelReadListener {
    /**
     * Notify the caller and returns the data
     *
     * @param result Import all the data generated after success
     */
    void notify(List<R> result);
}
