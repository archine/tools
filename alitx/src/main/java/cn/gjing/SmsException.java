package cn.gjing;

/**
 * @author Gjing
 * 短信异常
 **/
class SmsException extends RuntimeException {
    SmsException(String message) {
        super(message);
    }
}
