package cn.gjing.tools.auth.exception;

/**
 * Token parses and transforms exceptions
 *
 * @author Gjing
 **/
public class TokenAuthorizationException extends AuthException {
    public TokenAuthorizationException(String message) {
        super(message);
    }
}
