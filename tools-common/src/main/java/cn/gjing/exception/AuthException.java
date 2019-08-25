package cn.gjing.exception;

/**
 * @author Gjing
 **/
@Deprecated
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
