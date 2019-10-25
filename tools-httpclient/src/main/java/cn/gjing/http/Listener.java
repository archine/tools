package cn.gjing.http;

/**
 * Result Listener
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface Listener<T> {
    /**
     * notify listener
     *
     * @param t t
     */
    void notify(T t);
}
