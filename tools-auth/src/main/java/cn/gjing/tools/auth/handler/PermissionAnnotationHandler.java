package cn.gjing.tools.auth.handler;

import cn.gjing.tools.auth.annotation.RequiredPermissions;
import cn.gjing.tools.auth.exception.RoleAuthorizationException;
import cn.gjing.tools.auth.metadata.AuthorizationMetaData;

import java.lang.annotation.Annotation;

/**
 * Permission annotation handler to verify the permissions of a method that has
 * the {@link RequiredPermissions} annotation
 *
 * @author Gjing
 **/
public final class PermissionAnnotationHandler extends AnnotationHandler {
    public PermissionAnnotationHandler() {
        super(RequiredPermissions.class);
    }

    @Override
    public void assertAuthorization(Annotation annotation, AuthorizationMetaData metaData) {
        RequiredPermissions requiredPermissions = (RequiredPermissions) annotation;
        if (requiredPermissions.value().length > 0 && metaData.getPermissions() == null) {
            throw new RoleAuthorizationException("Insufficient permission");
        }
        if (requiredPermissions.value().length == 1) {
            if (!metaData.getPermissions().contains(requiredPermissions.value()[0])) {
                throw new RoleAuthorizationException("Insufficient permission");
            }
            return;
        }
        boolean isPass = false;
        for (String methodPermission : requiredPermissions.value()) {
            if (isPass) {
                break;
            }
            isPass = metaData.getPermissions().contains(methodPermission);
        }
        if (!isPass) {
            throw new RoleAuthorizationException("Insufficient permission");
        }
    }
}
