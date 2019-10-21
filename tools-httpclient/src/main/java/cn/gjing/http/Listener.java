package cn.gjing.http;

/**
 * 监听者
 * @author Gjing
 **/
@FunctionalInterface
public interface Listener<T> {
    void notify(T t);
}
