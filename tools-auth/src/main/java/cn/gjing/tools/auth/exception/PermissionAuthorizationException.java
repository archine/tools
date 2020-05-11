package cn.gjing.tools.auth.exception;

/**
 * Permission validation exception
 *
 * @author Gjing
 **/
public class PermissionAuthorizationException extends AuthException {
    public PermissionAuthorizationException(String message) {
        super(message);
    }
}
