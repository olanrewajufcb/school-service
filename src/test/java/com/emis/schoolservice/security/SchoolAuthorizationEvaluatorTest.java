package com.emis.schoolservice.security;

import com.emis.schoolservice.enums.ActorType;
import com.emis.schoolservice.enums.SchoolAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchoolAuthorizationEvaluatorTest {

    @Mock
    private ActorContextFactory actorContextFactory;

    @Mock
    private AuthorizationPolicy policy;

    @InjectMocks
    private SchoolAuthorizationEvaluator evaluator;

    private Authentication authentication;
    private ActorContext actorContext;

    @BeforeEach
    void setUp() {
        authentication = mock(Authentication.class);
        actorContext = ActorContext.builder()
                .type(ActorType.USER)
                .username("test-user")
                .build();
    }

    @Test
    void authorize_Success() {
        when(actorContextFactory.fromAuthentication(authentication))
                .thenReturn(Mono.just(actorContext));
        when(policy.isAuthorized(actorContext, "SCH-001", SchoolAction.CREATE_SCHOOL))
                .thenReturn(Mono.just(true));

        StepVerifier.create(evaluator.authorize(authentication, "SCH-001", SchoolAction.CREATE_SCHOOL))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void authorize_Denied() {
        when(actorContextFactory.fromAuthentication(authentication))
                .thenReturn(Mono.just(actorContext));
        when(policy.isAuthorized(actorContext, "SCH-001", SchoolAction.VIEW_SCHOOL))
                .thenReturn(Mono.just(false));

        StepVerifier.create(evaluator.authorize(authentication, "SCH-001", SchoolAction.VIEW_SCHOOL))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void authorize_FactoryError_PropagatesError() {
        when(actorContextFactory.fromAuthentication(authentication))
                .thenReturn(Mono.error(new IllegalStateException("Invalid auth")));

        StepVerifier.create(evaluator.authorize(authentication, "SCH-001", SchoolAction.CREATE_SCHOOL))
                .expectError(IllegalStateException.class)
                .verify();
    }
}
