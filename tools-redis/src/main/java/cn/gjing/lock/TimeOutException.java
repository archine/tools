package cn.gjing.lock;

/**
 * @author Gjing
 **/
public class TimeOutException extends RuntimeException {
    public TimeOutException(String message) {
        super(message);
    }
}
