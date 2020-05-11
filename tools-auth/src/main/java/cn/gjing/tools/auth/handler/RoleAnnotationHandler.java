package cn.gjing.tools.auth.handler;

import cn.gjing.tools.auth.annotation.RequiredRoles;
import cn.gjing.tools.auth.exception.RoleAuthorizationException;
import cn.gjing.tools.auth.metadata.AuthorizationMetaData;

import java.lang.annotation.Annotation;

/**
 * Permission annotation handler to verify the permissions of a method that has
 * the {@link RequiredRoles} annotation
 *
 * @author Gjing
 **/
public final class RoleAnnotationHandler extends AnnotationHandler {
    public RoleAnnotationHandler() {
        super(RequiredRoles.class);
    }

    @Override
    public void assertAuthorization(Annotation annotation, AuthorizationMetaData metaData) {
        RequiredRoles requiredRoles = (RequiredRoles) annotation;
        if (requiredRoles.value().length > 0 && metaData.getRoles() == null) {
            throw new RoleAuthorizationException("Insufficient role permission");
        }
        if (requiredRoles.value().length == 1) {
            if (!metaData.getRoles().contains(requiredRoles.value()[0])) {
                throw new RoleAuthorizationException("Insufficient role permission");
            }
            return;
        }
        boolean isPass = false;
        for (String methodRole : requiredRoles.value()) {
            if (isPass) {
                break;
            }
            isPass = metaData.getRoles().contains(methodRole);
        }
        if (!isPass) {
            throw new RoleAuthorizationException("Insufficient role permission");
        }
    }
}
