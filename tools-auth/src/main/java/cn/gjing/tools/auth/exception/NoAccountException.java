package cn.gjing.tools.auth.exception;

/**
 * No exception was found for the account
 *
 * @author Gjing
 **/
public class NoAccountException extends AuthException {
    public NoAccountException(String message) {
        super(message);
    }
}
