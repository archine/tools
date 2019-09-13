package cn.gjing.http;

/**
 * http exception
 * @author Gjing
 **/
public class HttpException extends RuntimeException {
    public HttpException(String message) {
        super(message);
    }
}
