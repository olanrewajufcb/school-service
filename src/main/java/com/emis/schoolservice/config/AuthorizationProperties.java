package com.emis.schoolservice.config;

import com.emis.schoolservice.enums.SchoolAction;
import com.emis.schoolservice.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "emis.authorization")
public class AuthorizationProperties {

    @NotNull
    private Map<SchoolAction, ActionPolicy> actions = new EnumMap<>(SchoolAction.class);


    @Getter
    @Setter
    public static class ActionPolicy {
        private final Set<UserRole> roles = EnumSet.noneOf(UserRole.class);
        private final Set<String> serviceAuthorities = new HashSet<>();
    }
}