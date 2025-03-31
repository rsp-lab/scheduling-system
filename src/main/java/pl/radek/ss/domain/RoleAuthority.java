package pl.radek.ss.domain;

import org.springframework.security.core.GrantedAuthority;

public class RoleAuthority implements GrantedAuthority
{
    private final String authority;
    
    public RoleAuthority(String authority) {
        this.authority = authority;
    }
    
    @Override
    public String getAuthority() {
        return authority;
    }
    
    @Override
    public String toString() {
        return authority;
    }
}
