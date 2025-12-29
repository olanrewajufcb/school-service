//package com.emis.shoolservice.config;
//
//import java.util.List;
//import java.util.Objects;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import reactor.core.publisher.Flux;
//
//@Configuration
//@EnableWebFluxSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        return http
//                .authorizeExchange(exchanges -> exchanges
//                        .pathMatchers("/actuator/**").permitAll()
//                        .pathMatchers("/swagger-ui.html").permitAll()
//                        .pathMatchers("/swagger-ui/index.html").permitAll()
//                        .pathMatchers("/swagger-ui/**").permitAll()
//                        .pathMatchers("/webjars/**").permitAll()
//                        .pathMatchers("/v3/api-docs/**").permitAll()
//
//                        .pathMatchers("/favicon.ico").permitAll()
//                        .pathMatchers("/index.html").permitAll()
//
//                        .pathMatchers("/api/v1/schools/**").authenticated()
//                        .pathMatchers("/api/v1/schools/**").authenticated()
//                        .anyExchange().authenticated()
//                )
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
//                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .build();
//    }
//
//    @Bean
//    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
//        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
//        converter.setJwtGrantedAuthoritiesConverter(this::extractAuthoritiesFromJwt);
//        return converter;
//    }
//    private Flux<GrantedAuthority> extractAuthoritiesFromJwt(Jwt jwt) {
//        List<String> roles = jwt.getClaimAsStringList("roles");
//        if(roles == null || roles.isEmpty()) return Flux.empty();
//
//        return Flux.fromStream(roles.stream()
//                .map(this::toGrantedAuthority)
//                .filter(Objects::nonNull));
//    }
//
//    private GrantedAuthority toGrantedAuthority(String role) {
//        if(role == null) return null;
//
//        String cleanedRole = role.trim();
//        if(cleanedRole.isEmpty()) return null;
//        String upper = cleanedRole.toUpperCase();
//        String prefixed = upper.startsWith("ROLE_") ? upper : "ROLE_" + upper;
//        return new SimpleGrantedAuthority(prefixed);
//
//    }
//}
