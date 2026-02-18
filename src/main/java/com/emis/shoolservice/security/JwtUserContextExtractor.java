// package com.emis.shoolservice.security;
//
//
// import java.util.Objects;
//
// import com.emis.shoolservice.enums.UserRole;
// import com.emis.shoolservice.exception.AccessDeniedException;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.ReactiveSecurityContextHolder;
// import org.springframework.security.core.context.SecurityContext;
// import org.springframework.security.oauth2.jwt.Jwt;
// import org.springframework.stereotype.Component;
// import reactor.core.publisher.Mono;
//
// @Slf4j
// @Component
// public class JwtUserContextExtractor {
//
//    public Mono<UserContext> extractUserContext() {
//    return ReactiveSecurityContextHolder.getContext()
//        .switchIfEmpty(Mono.error(new AccessDeniedException("No security context found")))
//        .map(SecurityContext::getAuthentication)
//        .filter(Authentication::isAuthenticated)
//        .switchIfEmpty(Mono.error(new AccessDeniedException("Unauthenticated access")))
//        .map(this::extractJwtFromAuthentication)
//        .map(this::buildUserContext)
//        .onErrorMap(err -> new AccessDeniedException("Unauthorized: " + err.getMessage()));
//    }
//
//    private Jwt extractJwtFromAuthentication(Authentication auth) {
//        if (!(auth.getPrincipal() instanceof Jwt jwt)) {
//            throw new AccessDeniedException("Invalid authentication principal");
//        }
//        return jwt;
//    }
//
//    private UserContext buildUserContext(Jwt jwt){
//        return new UserContext(
//                extractClaim(jwt, "username"),
//                extractClaim(jwt, "given_name"),
//                extractClaim(jwt, "family_name"),
//                 extractClaim(jwt, "school_code"),
//                extractUserRole(jwt),
//                extractClaim(jwt, "email"),
//                extractClaim(jwt, "lga"),
//                extractClaim(jwt, "state"));
//    }
//
//  private Set<UserRole> extractUserRoles(Jwt jwt) {
//    return jwt.getClaimAsStringList("roles")
//        .stream()
//        .map(String::trim)
//        .map(String::toUpperCase)
//        .map(role -> {
//            try {
//                return UserRole.valueOf(role);
//            } catch (Exception e) {
//                return UserRole.UNKNOWN;
//            }
//        })
//        .collect(Collectors.toSet());
// }
//
//
//  private String extractClaim(Jwt jwt, String claim){
//        return jwt.hasClaim(claim)? jwt.getClaimAsString(claim): null;
//  }
// }
