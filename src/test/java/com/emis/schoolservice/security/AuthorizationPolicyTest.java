package com.emis.schoolservice.security;

import com.emis.schoolservice.config.AuthorizationProperties;
import com.emis.schoolservice.enums.ActorType;
import com.emis.schoolservice.enums.SchoolAction;
import com.emis.schoolservice.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorizationPolicyTest {

    private AuthorizationPolicy policy;
    private AuthorizationProperties properties;

    @BeforeEach
    void setUp() {
        properties = new AuthorizationProperties();
        Map<SchoolAction, AuthorizationProperties.ActionPolicy> actions = new EnumMap<>(SchoolAction.class);

        // Setup Create School Policy
        AuthorizationProperties.ActionPolicy createPolicy = new AuthorizationProperties.ActionPolicy();
        createPolicy.getRoles().addAll(Set.of(UserRole.SYSTEM_ADMIN, UserRole.SCHOOL_ADMIN));
        createPolicy.getServiceAuthorities().add("CREATE_SCHOOL_SERVICE");
        actions.put(SchoolAction.CREATE_SCHOOL, createPolicy);

        // Setup View School Policy
        AuthorizationProperties.ActionPolicy viewPolicy = new AuthorizationProperties.ActionPolicy();
        viewPolicy.getRoles().addAll(Set.of(UserRole.SYSTEM_ADMIN, UserRole.SCHOOL_ADMIN, UserRole.SCHOOL_STAFF));
        viewPolicy.getServiceAuthorities().add("VIEW_SCHOOL_SERVICE");
        actions.put(SchoolAction.VIEW_SCHOOL, viewPolicy);

        properties.setActions(actions);
        policy = new AuthorizationPolicy(properties);
    }

    @Test
    void isAuthorized_User_MatchSchoolAndRole_ReturnsTrue() {
        ActorContext ctx = ActorContext.builder()
                .type(ActorType.USER)
                .schoolCode("SCH-001")
                .userRoles(Set.of(UserRole.SCHOOL_ADMIN))
                .build();

        StepVerifier.create(policy.isAuthorized(ctx, "SCH-001", SchoolAction.CREATE_SCHOOL))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isAuthorized_User_SystemAdmin_BypassSchoolCheck_ReturnsTrue() {
        ActorContext ctx = ActorContext.builder()
                .type(ActorType.USER)
                .schoolCode("OTHER-SCHOOL")
                .userRoles(Set.of(UserRole.SYSTEM_ADMIN))
                .build();

        StepVerifier.create(policy.isAuthorized(ctx, "SCH-001", SchoolAction.CREATE_SCHOOL))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isAuthorized_User_MismatchedSchool_ReturnsFalse() {
        ActorContext ctx = ActorContext.builder()
                .type(ActorType.USER)
                .schoolCode("OTHER-SCHOOL")
                .userRoles(Set.of(UserRole.SCHOOL_ADMIN))
                .build();

        StepVerifier.create(policy.isAuthorized(ctx, "SCH-001", SchoolAction.CREATE_SCHOOL))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void isAuthorized_User_InsufficientRole_ReturnsFalse() {
        ActorContext ctx = ActorContext.builder()
                .type(ActorType.USER)
                .schoolCode("SCH-001")
                .userRoles(Set.of(UserRole.SCHOOL_STAFF))
                .build();

        StepVerifier.create(policy.isAuthorized(ctx, "SCH-001", SchoolAction.CREATE_SCHOOL))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void isAuthorized_Service_ValidAuthority_ReturnsTrue() {
        ActorContext ctx = ActorContext.builder()
                .type(ActorType.SERVICE)
                .serviceAuthorities(Set.of("CREATE_SCHOOL_SERVICE"))
                .build();

        StepVerifier.create(policy.isAuthorized(ctx, "SCH-001", SchoolAction.CREATE_SCHOOL))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isAuthorized_Service_InvalidAuthority_ReturnsFalse() {
        ActorContext ctx = ActorContext.builder()
                .type(ActorType.SERVICE)
                .serviceAuthorities(Set.of("WRONG_SERVICE"))
                .build();

        StepVerifier.create(policy.isAuthorized(ctx, "SCH-001", SchoolAction.CREATE_SCHOOL))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void isAuthorized_MissingPolicy_ReturnsFalse() {
        ActorContext ctx = ActorContext.builder()
                .type(ActorType.USER)
                .schoolCode("SCH-001")
                .userRoles(Set.of(UserRole.SYSTEM_ADMIN))
                .build();

        // Remove policy for an action
        properties.getActions().remove(SchoolAction.CREATE_SCHOOL);

        StepVerifier.create(policy.isAuthorized(ctx, "SCH-001", SchoolAction.CREATE_SCHOOL))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void validatePolicies_AllActionsPresent_Success() {
        // All actions are present in properties in setUp
        policy.validatePolicies();
    }

    @Test
    void validatePolicies_MissingAction_ThrowsException() {
        properties.getActions().remove(SchoolAction.VIEW_SCHOOL);
        assertThatThrownBy(() -> policy.validatePolicies())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Missing authorization policy for action");
    }
}
