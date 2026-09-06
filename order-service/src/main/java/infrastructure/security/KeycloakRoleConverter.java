package infrastructure.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<String> roles = new HashSet<>();

        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            Object realmRoles = realmAccess.get("roles");
            if (realmRoles instanceof Collection<?>) {
                ((Collection<?>) realmRoles).stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .forEach(roles::add);
            }
        }

        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            for (Object clientEntry : resourceAccess.values()) {
                if (clientEntry instanceof Map<?, ?> clientMap && clientMap.containsKey("roles")) {
                    Object clientRoles = clientMap.get("roles");
                    if (clientRoles instanceof Collection<?>) {
                        ((Collection<?>) clientRoles).stream()
                                .filter(String.class::isInstance)
                                .map(String.class::cast)
                                .forEach(roles::add);
                    }
                }
            }
        }
        return roles.stream()
                .map(String::toUpperCase)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
