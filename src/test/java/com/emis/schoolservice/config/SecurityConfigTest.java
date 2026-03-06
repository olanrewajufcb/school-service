package com.emis.schoolservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

    private SecurityConfig securityConfig;
    private ReactiveJwtAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
        converter = securityConfig.jwtAuthenticationConverter();
    }

    @Test
    void jwtAuthenticationConverter_WithRoles_ConvertsToAuthorities() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.hasClaim("roles")).thenReturn(true);
        when(jwt.getClaim("roles")).thenReturn(List.of("admin", "user"));
        when(jwt.getClaimAsString("roles")).thenReturn("admin,user");
        when(jwt.getClaimAsString("preferred_username")).thenReturn("john.doe");

        Flux<GrantedAuthority> authoritiesFlux = (Flux<GrantedAuthority>) converter.convert(jwt).flatMapMany(auth -> Flux.fromIterable(auth.getAuthorities()));

        StepVerifier.create(authoritiesFlux)
                .expectNextMatches(ga -> ga.getAuthority().equals("ROLE_ADMIN"))
                .expectNextMatches(ga -> ga.getAuthority().equals("ROLE_USER"))
                .expectNextMatches(ga -> ga.getAuthority().equals("ACTOR_USER"))
                .verifyComplete();
    }

    @Test
    void jwtAuthenticationConverter_NoRoles_AssignsActorUser() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.hasClaim("roles")).thenReturn(false);
        when(jwt.getClaimAsString("roles")).thenReturn(null);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("john.doe");

        Flux<GrantedAuthority> authoritiesFlux = (Flux<GrantedAuthority>) converter.convert(jwt).flatMapMany(auth -> Flux.fromIterable(auth.getAuthorities()));

        StepVerifier.create(authoritiesFlux)
                .expectNextMatches(ga -> ga.getAuthority().equals("ACTOR_USER"))
                .verifyComplete();
    }

    @Test
    void jwtAuthenticationConverter_InternalServiceRole_AssignsActorService() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.hasClaim("roles")).thenReturn(true);
        when(jwt.getClaim("roles")).thenReturn(List.of("INTERNAL_SERVICE"));
        when(jwt.getClaimAsString("roles")).thenReturn("INTERNAL_SERVICE");
        when(jwt.getClaimAsString("preferred_username")).thenReturn("service-x");

        Flux<GrantedAuthority> authoritiesFlux = (Flux<GrantedAuthority>) converter.convert(jwt).flatMapMany(auth -> Flux.fromIterable(auth.getAuthorities()));

        StepVerifier.create(authoritiesFlux)
                .expectNextMatches(ga -> ga.getAuthority().equals("ROLE_INTERNAL_SERVICE"))
                .expectNextMatches(ga -> ga.getAuthority().equals("ACTOR_SERVICE"))
                .verifyComplete();
    }

    @Test
    void jwtAuthenticationConverter_ServiceAccountUsername_AssignsActorService() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.hasClaim("roles")).thenReturn(false);
        when(jwt.getClaimAsString("roles")).thenReturn(null);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("service-account-abc");

        Flux<GrantedAuthority> authoritiesFlux = (Flux<GrantedAuthority>) converter.convert(jwt).flatMapMany(auth -> Flux.fromIterable(auth.getAuthorities()));

        StepVerifier.create(authoritiesFlux)
                .expectNextMatches(ga -> ga.getAuthority().equals("ACTOR_SERVICE"))
                .verifyComplete();
    }

    @Test
    void isServiceToken_ChecksBothRolesAndUsername() {
        // Since isServiceToken is private, we test it through the behavior of the converter
        
        // Case 1: Just internal service role
        Jwt jwt1 = mock(Jwt.class);
        when(jwt1.hasClaim("roles")).thenReturn(false); // We don't care about "roles" claim for isServiceToken's getClaimAsString("roles")
        when(jwt1.getClaimAsString("roles")).thenReturn("SOME_ROLE,INTERNAL_SERVICE");
        when(jwt1.getClaimAsString("preferred_username")).thenReturn("user");
        
        // Case 2: Just service-account username
        Jwt jwt2 = mock(Jwt.class);
        when(jwt2.hasClaim("roles")).thenReturn(false);
        when(jwt2.getClaimAsString("roles")).thenReturn("USER");
        when(jwt2.getClaimAsString("preferred_username")).thenReturn("service-account-123");

        // Case 3: Neither
        Jwt jwt3 = mock(Jwt.class);
        when(jwt3.hasClaim("roles")).thenReturn(false);
        when(jwt3.getClaimAsString("roles")).thenReturn("USER");
        when(jwt3.getClaimAsString("preferred_username")).thenReturn("user");

        // Test through converter
        StepVerifier.create(converter.convert(jwt1).flatMapMany(auth -> Flux.fromIterable(auth.getAuthorities())))
                .expectNextMatches(ga -> ga.getAuthority().equals("ACTOR_SERVICE"))
                .verifyComplete();

        StepVerifier.create(converter.convert(jwt2).flatMapMany(auth -> Flux.fromIterable(auth.getAuthorities())))
                .expectNextMatches(ga -> ga.getAuthority().equals("ACTOR_SERVICE"))
                .verifyComplete();

        StepVerifier.create(converter.convert(jwt3).flatMapMany(auth -> Flux.fromIterable(auth.getAuthorities())))
                .expectNextMatches(ga -> ga.getAuthority().equals("ACTOR_USER"))
                .verifyComplete();
    }
}
