package cn.gjing;

/**
 * @author Gjing
 * 短信异常
 **/
class SmsException extends RuntimeException {
    public SmsException(String message) {
        super(message);
    }
}
