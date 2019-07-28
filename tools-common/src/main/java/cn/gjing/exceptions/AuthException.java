package cn.gjing.exceptions;

/**
 * @author Gjing
 **/
@Deprecated
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
