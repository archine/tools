package cn.gjing.lock;

/**
 * @author Gjing
 **/
public class TimeoutException extends RuntimeException {
    public TimeoutException(String message) {
        super(message);
    }
}
