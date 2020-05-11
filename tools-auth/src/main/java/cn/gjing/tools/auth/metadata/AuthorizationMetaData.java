package cn.gjing.tools.auth.metadata;

import java.util.Collection;

/**
 * Permission metadata, which corresponds to the permissions the user has,
 * can exist anywhere
 *
 * @author Gjing
 **/
public interface AuthorizationMetaData {
    /**
     * Gets all the roles corresponding to this user
     *
     * @return All roles
     */
    Collection<String> getRoles();

    /**
     * Gets all the permissions corresponding to this user
     *
     * @return All permissions
     */
    Collection<String> getPermissions();
}
