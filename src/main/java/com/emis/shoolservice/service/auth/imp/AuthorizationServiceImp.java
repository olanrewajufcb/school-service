package com.emis.shoolservice.service.auth.imp;


import java.nio.file.AccessDeniedException;

import com.emis.shoolservice.security.UserContext;
import com.emis.shoolservice.service.auth.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AuthorizationServiceImp implements AuthorizationService {

  @Override
  public Mono<Void> canCreateSchool(UserContext context) {
    return switch (context.role()) {
      case SYSTEM_ADMIN -> Mono.empty();
      case STATE_ADMIN, LGA_ADMIN, SCHOOL_ADMIN -> Mono.error(new AccessDeniedException(
              "User " + context.username() + " with role "  + context.role() +
                      " can not register schools"));
      default -> {
        log.warn(
            "User {} with role  {} attempted to create school without permission",
            context.username(),
            context.role());
        yield Mono.error(
            new AccessDeniedException(
                    "User " + context.username() + " with role "  + context.role() +
                            " does not have permission to register schools"));
      }
    };
  }

}
