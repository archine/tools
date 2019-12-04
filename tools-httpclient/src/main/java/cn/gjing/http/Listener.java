package cn.gjing.http;

/**
 * @author Gjing
 **/
@FunctionalInterface
public interface Listener<T> {
    /**
     * Result listener
     * @param t result
     */
    void notify(T t);
}
