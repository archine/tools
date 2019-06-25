package cn.gjing.lock;

/**
 * @author Gjing
 **/
public class TimeoutException extends RuntimeException {
    private static final long serialVersionUID = -2055408728917203161L;

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException() {
        super();
    }
}
