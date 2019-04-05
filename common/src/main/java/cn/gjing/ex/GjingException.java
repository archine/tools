package cn.gjing.ex;

/**
 * @author Gjing
 **/
public class GjingException extends RuntimeException {

    public GjingException(String message) {
        super(message);
    }

    public GjingException() {
        super();
    }
}
