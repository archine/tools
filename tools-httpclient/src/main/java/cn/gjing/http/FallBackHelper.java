package cn.gjing.http;

/**
 * @author Gjing
 **/
@FunctionalInterface
public interface FallBackHelper<T> {
    /**
     * Exception fallback
     * @param t Exception error info
     */
    void fallback(T t);
}
