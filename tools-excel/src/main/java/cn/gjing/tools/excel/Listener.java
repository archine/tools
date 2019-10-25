package cn.gjing.tools.excel;

/**
 * Result listener
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface Listener<T> {
    /**
     * Notify listener
     *
     * @param t t
     */
    void notify(T t);
}
