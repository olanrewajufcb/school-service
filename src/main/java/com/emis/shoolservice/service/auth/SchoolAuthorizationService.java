//package com.emis.shoolservice.service.auth;
//
//import org.springframework.stereotype.Component;
//
//@Component("schoolAuth")
//@RequiredArgsConstructor
//public class SchoolAuthorizationService {
//    private final AuthorizationPolicy policy;
//
//    public boolean authorize(
//            Authentication authentication,
//            String schoolCode,
//            SchoolAction action
//    ) {
//        Jwt jwt = (Jwt) authentication.getPrincipal();
//        UserContext ctx = UserContext.from(jwt);
//
//        return policy.isAuthorized(ctx, schoolCode, action);
//    }
//
//    @Component
//    public class AuthorizationPolicy {
//
//        private static final Map<SchoolAction, Set<UserRole>> ACTION_POLICIES =
//                Map.of(
//                        SchoolAction.CREATE_FACILITY, Set.of(SYSTEM_ADMIN, STATE_ADMIN, SCHOOL_ADMIN),
//                        SchoolAction.CREATE_STAFF,    Set.of(SYSTEM_ADMIN, STATE_ADMIN, SCHOOL_ADMIN),
//                        SchoolAction.CREATE_STUDENT,  Set.of(SYSTEM_ADMIN, STATE_ADMIN, LGA_ADMIN, SCHOOL_ADMIN),
//                        SchoolAction.VIEW_STUDENT,    Set.of(SYSTEM_ADMIN, STATE_ADMIN, LGA_ADMIN, SCHOOL_ADMIN, TEACHER)
//                );
//
//        public boolean isAuthorized(
//                UserContext ctx,
//                String schoolCode,
//                SchoolAction action
//        ) {
//
//            if (!hasSchoolScope(ctx, schoolCode)) {
//                return false;
//            }
//
//            Set<UserRole> allowed = ACTION_POLICIES.get(action);
//            if (allowed == null) {
//                return false;
//            }
//
//            return ctx.roles().stream().anyMatch(allowed::contains);
//        }
//
//        private boolean hasSchoolScope(UserContext ctx, String schoolCode) {
//
//            if (ctx.hasRole(SYSTEM_ADMIN)) return true;
//            if (ctx.hasRole(STATE_ADMIN))  return true;
//            if (ctx.hasRole(LGA_ADMIN))    return true;
//
//            return schoolCode.equals(ctx.schoolCode());
//        }
//    }
//
//
//    @PreAuthorize("@schoolAuth.canAccessSchool(authentication, #schoolCode)")
//    @GetMapping("/api/v1/schools/{schoolCode}/students")
//    public Flux<StudentDto> students(@PathVariable String schoolCode) {
//        return service.students(schoolCode);
//    }
//
//}
