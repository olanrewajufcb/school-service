package com.emis.schoolservice.security;

import com.emis.schoolservice.enums.SchoolAction;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("schoolAuth")
@RequiredArgsConstructor
public class SchoolAuthorizationEvaluator {

    private final ActorContextFactory actorContextFactory;
    private final AuthorizationPolicy policy;

    public Mono<Boolean> authorize(Authentication authentication,
                                   String schoolCode, SchoolAction action) {
        return actorContextFactory
                .fromAuthentication(authentication)
                .flatMap(actor -> policy
                        .isAuthorized(actor, schoolCode, action));
    }
}