package com.emis.schoolservice.security;

import com.emis.schoolservice.enums.ActorType;
import com.emis.schoolservice.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class ActorContext {

  private final ActorType type;

  private final String username;
  private final String schoolCode;
  private final Set<UserRole> userRoles;

  private final String clientId;
  private final Set<String> serviceAuthorities;

  public boolean isUser() {
    return type == ActorType.USER;
  }

  public boolean isService() {
    return type == ActorType.SERVICE;
  }
}