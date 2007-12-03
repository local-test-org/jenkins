package hudson.security;

import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * {@link Authentication} implementation for {@link Principal}
 * given through {@link HttpServletRequest}.
 *
 * <p>
 * This is used to plug the container authentication to Acegi,
 * for backward compatibility with Hudson &lt; 1.160.
 *
 * @author Kohsuke Kawaguchi
 */
public final class ContainerAuthentication implements Authentication {
    private final HttpServletRequest request;

    public ContainerAuthentication(HttpServletRequest request) {
        this.request = request;
    }

    public GrantedAuthority[] getAuthorities() {
        // Servlet API doesn't provide a way to list up all roles the current user
        // has, so we are approximating the current user's capability by checking
        // the 'admin' role.
        if (request.isUserInRole("admin"))
            return ADMIN_AUTHORITY;
        else
            return NO_AUTHORITY;
    }

    public Object getCredentials() {
        return null;
    }

    public Object getDetails() {
        return null;
    }

    public Object getPrincipal() {
        Principal principal = request.getUserPrincipal();
        if(principal!=null)
            return principal;
        else
            return "anonymous";
    }

    public boolean isAuthenticated() {
        return true;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        // noop
    }

    public String getName() {
        Principal principal = request.getUserPrincipal();
        if(principal!=null)
            return principal.getName();
        else
            return "anonymous";
    }

    private static final GrantedAuthority[] ADMIN_AUTHORITY = {new GrantedAuthorityImpl("admin")};
    // Acegi doesn't like empty array, so we need to set something
    private static final GrantedAuthority[] NO_AUTHORITY = {new GrantedAuthorityImpl("authenticated")};
}
