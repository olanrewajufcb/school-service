package com.emis.schoolservice.security;

import com.emis.schoolservice.enums.ActorType;
import com.emis.schoolservice.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ActorContextFactory {

    public Mono<ActorContext> fromAuthentication(Authentication authentication) {

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            return Mono.error(new IllegalStateException("Invalid authentication type"));
        }

        Jwt jwt = jwtAuth.getToken();

        return Mono.defer(() -> {

            String clientId = jwt.getClaimAsString("client_id");
            String subject = jwt.getSubject();

            boolean isService = isServiceToken(jwt);

            if (isService) {
                return buildServiceActor(jwt, clientId);
            } else {
                return buildUserActor(jwt, subject);
            }
        });
    }

    private boolean isServiceToken(Jwt jwt) {
        String clientId  = jwt.getClaimAsString("client_id");
        String preferredUsername = jwt.getClaimAsString("preferred_username");

        return clientId != null &&
                (preferredUsername == null
                        || preferredUsername.startsWith("service-account"));
    }

    private Mono<ActorContext> buildUserActor(Jwt jwt, String username) {

        return Mono.just(
                ActorContext.builder()
                        .type(ActorType.USER)
                        .username(username)
                        .schoolCode(jwt.getClaimAsString("school_code"))
                        .userRoles(extractRealmRoles(jwt))
                        .build()
        );
    }

    private Set<UserRole> extractRealmRoles(Jwt jwt) {
        Set<UserRole> roles = new HashSet<>();

        // 1. Extract from root 'roles' claim (found in user's JWT)
        if (jwt.hasClaim("roles")) {
            roles.addAll(parseRoles(jwt.getClaim("roles")));
        }

        // 2. Extract from 'realm_access.roles' (standard Keycloak)
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            roles.addAll(parseRoles(realmAccess.get("roles")));
        }

        return roles;
    }

    private Set<UserRole> parseRoles(Object rolesObject) {
        if (!(rolesObject instanceof Collection<?> roles)) {
            return Set.of();
        }

        return roles.stream()
                .filter(role -> role instanceof String)
                .map(role -> (String) role)
                .filter(role -> !role.trim().isEmpty())
                .map(String::toUpperCase)
                .map(role -> {
                    try {
                        return UserRole.valueOf(role);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Mono<ActorContext> buildServiceActor(Jwt jwt, String clientId) {

        return Mono.just(
                ActorContext.builder()
                        .type(ActorType.SERVICE)
                        .clientId(clientId)
                        .serviceAuthorities(extractClientRoles(jwt,clientId))
                        .build()
        );
    }

    private Set<String> extractClientRoles(Jwt jwt, String clientId) {

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null || !resourceAccess.containsKey(clientId)) {
            return Set.of();
        }

        Map<String, Object> clientAccess =
                (Map<String, Object>) resourceAccess.get(clientId);

        Collection<String> roles =
                (Collection<String>) clientAccess.get("roles");

        return roles == null
                ? Set.of()
                : new HashSet<>(roles);
    }
}