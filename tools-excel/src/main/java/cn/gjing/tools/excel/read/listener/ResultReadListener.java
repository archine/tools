package cn.gjing.tools.excel.read.listener;

import java.util.List;

/**
 * All data imports were successful
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ResultReadListener<R> extends ReadListener<R> {
    /**
     * Notifies the caller and returns the result
     *
     * @param result All data
     */
    void notify(List<R> result);
}
