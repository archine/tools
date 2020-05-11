package cn.gjing.tools.auth.metadata;

import cn.gjing.tools.auth.exception.PermissionAuthorizationException;
import cn.gjing.tools.auth.exception.RoleAuthorizationException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Gjing
 **/
public class SimpleAuthorizationMetaData implements AuthorizationMetaData {

    private Set<String> roles;
    private Set<String> permissions;

    /**
     * Add a role to the current user to match the method,
     * and a failure to match throws an {@link RoleAuthorizationException}
     *
     * @param role role
     */
    public void addRole(String role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
    }

    /**
     * Add a roles to the current user to match the method,
     * and a failure to match throws an {@link RoleAuthorizationException}
     *
     * @param roles roles
     */
    public void addRole(Collection<String> roles) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.addAll(roles);
    }

    /**
     * Add a permission to the current user to match the method,
     * and a failure to match throws an {@link PermissionAuthorizationException}
     *
     * @param permission permission
     */
    public void addPermission(String permission) {
        if (this.permissions == null) {
            this.permissions = new HashSet<>();
        }
        this.permissions.add(permission);
    }

    /**
     * Add a permission to the current user to match the method,
     * and a failure to match throws an {@link PermissionAuthorizationException}
     *
     * @param permissions permissions
     */
    public void addPermission(Collection<String> permissions) {
        if (this.permissions == null) {
            this.permissions = new HashSet<>();
        }
        this.permissions.addAll(permissions);
    }

    @Override
    public Collection<String> getRoles() {
        return this.roles;
    }

    @Override
    public Collection<String> getPermissions() {
        return this.permissions;
    }
}
