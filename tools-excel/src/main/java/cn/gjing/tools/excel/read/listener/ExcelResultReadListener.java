package cn.gjing.tools.excel.read.listener;

import cn.gjing.tools.excel.read.resolver.ExcelReader;

import java.util.List;

/**
 * Import completion result listener. If not set, no data will be collected.
 * You can set it by {@link ExcelReader#subscribe(ExcelResultReadListener)}
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelResultReadListener<R> {
    /**
     * Notify the caller and returns the data
     *
     * @param result Import all the data generated after success
     */
    void notify(List<R> result);
}
