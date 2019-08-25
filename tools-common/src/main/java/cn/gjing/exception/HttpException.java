package cn.gjing.exception;

/**
 * @author Gjing
 **/
public class HttpException extends RuntimeException {
    public HttpException(String message) {
        super(message);
    }
}
