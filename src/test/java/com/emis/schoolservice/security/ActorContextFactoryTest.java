package com.emis.schoolservice.security;

import com.emis.schoolservice.enums.ActorType;
import com.emis.schoolservice.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ActorContextFactoryTest {

    private ActorContextFactory factory;

    @BeforeEach
    void setUp() {
        factory = new ActorContextFactory();
    }

    @Test
    void fromAuthentication_User_Success() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "user123")
                .claim("preferred_username", "user123")
                .claim("school_code", "SCH-001")
                .claim("roles", List.of("SCHOOL_ADMIN", "SCHOOL_STAFF"))
                .build();
        Authentication auth = new JwtAuthenticationToken(jwt);

        StepVerifier.create(factory.fromAuthentication(auth))
                .assertNext(ctx -> {
                    assertThat(ctx.getType()).isEqualTo(ActorType.USER);
                    assertThat(ctx.getUsername()).isEqualTo("user123");
                    assertThat(ctx.getSchoolCode()).isEqualTo("SCH-001");
                    assertThat(ctx.getUserRoles()).containsExactlyInAnyOrder(UserRole.SCHOOL_ADMIN, UserRole.SCHOOL_STAFF);
                    assertThat(ctx.isUser()).isTrue();
                    assertThat(ctx.isService()).isFalse();
                })
                .verifyComplete();
    }

    @Test
    void fromAuthentication_Service_Success() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "service-account-client")
                .claim("preferred_username", "service-account-client")
                .claim("roles", List.of("READ_SCHOOL", "WRITE_SCHOOL"))
                .build();
        Authentication auth = new JwtAuthenticationToken(jwt);

        StepVerifier.create(factory.fromAuthentication(auth))
                .assertNext(ctx -> {
                    assertThat(ctx.getType()).isEqualTo(ActorType.SERVICE);
                    assertThat(ctx.getServiceName()).isEqualTo("service-account-client");
                    assertThat(ctx.getServiceAuthorities()).containsExactlyInAnyOrder("READ_SCHOOL", "WRITE_SCHOOL");
                    assertThat(ctx.isService()).isTrue();
                    assertThat(ctx.isUser()).isFalse();
                })
                .verifyComplete();
    }

    @Test
    void fromAuthentication_ServiceTokenNoGrantType_Success() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "service-account-client")
                .claim("preferred_username", "service-account-client")
                .claim("roles", List.of("READ_SCHOOL"))
                .build();
        Authentication auth = new JwtAuthenticationToken(jwt);

        StepVerifier.create(factory.fromAuthentication(auth))
                .assertNext(ctx -> {
                    assertThat(ctx.getType()).isEqualTo(ActorType.SERVICE);
                    assertThat(ctx.getServiceName()).isEqualTo("service-account-client");
                })
                .verifyComplete();
    }

    @Test
    void fromAuthentication_InvalidAuthenticationType_ReturnsError() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "pass");

        StepVerifier.create(factory.fromAuthentication(auth))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void fromAuthentication_UserMissingRoles_ReturnsEmptyRoles() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "user123")
                .claim("preferred_username", "user123")
                // No realm_access
                .build();
        Authentication auth = new JwtAuthenticationToken(jwt);

        StepVerifier.create(factory.fromAuthentication(auth))
                .assertNext(ctx -> {
                    assertThat(ctx.getUserRoles()).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    void fromAuthentication_UserWithRootRoles_Success() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "admin")
                .claim("preferred_username", "admin")
                .claim("roles", List.of("SYSTEM_ADMIN"))
                .build();
        Authentication auth = new JwtAuthenticationToken(jwt);

        StepVerifier.create(factory.fromAuthentication(auth))
                .assertNext(ctx -> {
                    assertThat(ctx.getType()).isEqualTo(ActorType.USER);
                    assertThat(ctx.getUserRoles()).containsExactlyInAnyOrder(UserRole.SYSTEM_ADMIN);
                })
                .verifyComplete();
    }

    @Test
    void fromAuthentication_ServiceMissingRoles_ReturnsEmptyAuthorities() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "service-account-client")
                .claim("preferred_username", "service-account-client")
                // No roles
                .build();
        Authentication auth = new JwtAuthenticationToken(jwt);

        StepVerifier.create(factory.fromAuthentication(auth))
                .assertNext(ctx -> {
                    assertThat(ctx.getServiceAuthorities()).isEmpty();
                })
                .verifyComplete();
    }
}
