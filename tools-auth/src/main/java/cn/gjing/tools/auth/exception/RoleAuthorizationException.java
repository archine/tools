package cn.gjing.tools.auth.exception;

/**
 * Role validation exception
 *
 * @author Gjing
 **/
public class RoleAuthorizationException extends AuthException {
    public RoleAuthorizationException(String message) {
        super(message);
    }
}
