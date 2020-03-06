package cn.gjing.tools.excel.listen;

/**
 * Monitor excel import successfully
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface ReadListener<T> {
    /**
     * Callback after successful import
     * @param val Get the data
     */
    void notify(T val);
}
