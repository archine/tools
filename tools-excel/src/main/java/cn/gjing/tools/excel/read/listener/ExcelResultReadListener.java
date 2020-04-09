package cn.gjing.tools.excel.read.listener;

import cn.gjing.tools.excel.read.resolver.ExcelReader;

import java.util.List;

/**
 * Import completion result listener,
 * which is only valid if set using the subscription method
 * @see ExcelReader
 * @author Gjing
 **/
@FunctionalInterface
public interface ExcelResultReadListener<R> extends ExcelReadListener {
    /**
     * Notifies the caller and returns the result
     *
     * @param result Import all the Java objects generated after success
     */
    void notify(List<R> result);
}
