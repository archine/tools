package cn.gjing.tools.auth;

import cn.gjing.tools.auth.metadata.AuthorizationMetaData;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * Authorization listener through which permissions are added to the current operating user
 *
 * @author Gjing
 **/
public interface AuthorizationListener {
    /**
     * This method is triggered before permission annotation processing.
     * This method is used to add permissions to user
     *
     * @param token   Request token
     * @return AuthorizationMetaData
     */
    AuthorizationMetaData supplyAccess(String token);

    /**
     * Authorization, which is triggered after the permission annotation is processed,
     * allows you to do some other authentication in this method
     *
     * @param token Request token
     */
    default void authentication(String token) {
    }

    /**
     * Execute after authentication is successful,
     * after the authorization annotation and authentication method are executed,
     * and the method is executed
     *
     * @param request HttpServletRequest
     * @param method  Request method
     */
    default void authenticationSuccess(HttpServletRequest request, Method method) {
    }
}
