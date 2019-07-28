package cn.gjing.exceptions;

/**
 * @author Gjing
 * 转换异常
 **/
public class CastException extends RuntimeException {
    public CastException(String message) {
        super(message);
    }
}
