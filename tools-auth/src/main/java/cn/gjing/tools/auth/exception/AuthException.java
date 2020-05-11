package cn.gjing.tools.auth.exception;

/**
 * Authorization exception
 *
 * @author Gjing
 **/
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
