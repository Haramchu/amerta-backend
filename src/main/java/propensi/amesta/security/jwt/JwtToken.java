package propensi.amesta.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import propensi.amesta.security.service.UserDetailsServiceImpl;

public class JwtToken extends AbstractAuthenticationToken {
    private final UserDetailsServiceImpl userDetails;

    public JwtToken(UserDetailsServiceImpl userDetails) {
        super(userDetails.getAuthorities());
        this.userDetails = userDetails;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }
}
