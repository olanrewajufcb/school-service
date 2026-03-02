package com.emis.schoolservice.security;

import com.emis.schoolservice.config.AuthorizationProperties;
import com.emis.schoolservice.enums.SchoolAction;
import com.emis.schoolservice.enums.UserRole;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorizationPolicy {

    private final AuthorizationProperties properties;

    public Mono<Boolean> isAuthorized(
            ActorContext ctx,
            String schoolCode,
            SchoolAction action
    ) {

        if (ctx.isService()) {
            return Mono.just(authorizeService(ctx, action));
        }

        return Mono.just(authorizeUser(ctx, schoolCode, action));
    }

    private boolean authorizeUser(
            ActorContext ctx,
            String schoolCode,
            SchoolAction action
    ) {

        if (!hasSchoolScope(ctx, schoolCode)) {
            return false;
        }

        AuthorizationProperties.ActionPolicy policy =
                properties.getActions().get(action);

        if (policy == null) {
            return false;
        }

        return ctx.getUserRoles()
                .stream()
                .anyMatch(policy.getRoles()::contains);
    }

    private boolean hasSchoolScope(ActorContext ctx, String schoolCode) {

        if (ctx.getUserRoles().contains(UserRole.SYSTEM_ADMIN)) return true;
        if (ctx.getUserRoles().contains(UserRole.STATE_ADMIN))  return true;
        if (ctx.getUserRoles().contains(UserRole.LGA_ADMIN))    return true;

        return schoolCode.equals(ctx.getSchoolCode());
    }

    private boolean authorizeService(
            ActorContext ctx,
            SchoolAction action
    ) {

        AuthorizationProperties.ActionPolicy policy =
                properties.getActions().get(action);

        if (policy == null) {
            return false;
        }

        return ctx.getServiceAuthorities()
                .stream()
                .anyMatch(policy.getServiceAuthorities()::contains);
    }

    @PostConstruct
    void validatePolicies() {
        for (SchoolAction action : SchoolAction.values()) {
            if (!properties.getActions().containsKey(action)) {
                log.error("Missing authorization policy for action: {}", action);
                throw new IllegalStateException(
                        "Missing authorization policy for action: " + action);
            }
        }
    }
}