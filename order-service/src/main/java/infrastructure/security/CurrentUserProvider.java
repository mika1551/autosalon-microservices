package infrastructure.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("currentUserProvider")
public class CurrentUserProvider {

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            throw new AccessDeniedException("No JwtAuthenticationToken found");
        }
        Jwt jwt = jwtAuthenticationToken.getToken();
        String userId = jwt.getClaimAsString("user_id");
        if (userId == null || userId.isBlank()) {
            userId = jwt.getSubject();
        }
        if (userId == null || userId.isBlank()) {
            throw new AccessDeniedException("Token does not contain user_id/sub");
        }
        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException ex) {
            throw new AccessDeniedException("Token user_id/sub is not UUID");
        }
    }
}
