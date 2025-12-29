package com.emis.shoolservice.service.auth;


import com.emis.shoolservice.security.UserContext;
import reactor.core.publisher.Mono;

public interface AuthorizationService {
    Mono<Void> canCreateSchool(UserContext context);
}
