package cn.gjing.ex;

/**
 * @author Gjing
 * 短信异常
 **/
public class SmsException extends RuntimeException {
    public SmsException(String message) {
        super(message);
    }
}
