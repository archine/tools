package cn.gjing.tools.excel.read.listener;

import cn.gjing.tools.excel.read.resolver.ExcelReader;

import java.util.List;

/**
 * Import completion result listener. If not set, no data will be collected.
 * You can set it by subscript method{@link ExcelReader#subscribe(ExcelResultReadListener)}
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelResultReadListener<R> {
    /**
     * Notifies the caller and returns the result
     *
     * @param result Import all the Java objects generated after success
     */
    void notify(List<R> result);
}
