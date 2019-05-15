package cn.gjing.ex;

/**
 * @author Gjing
 **/
public class NoAuthException extends RuntimeException {
    public NoAuthException(String message) {
        super(message);
    }
}
