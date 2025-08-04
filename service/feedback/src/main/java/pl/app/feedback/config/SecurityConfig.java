package pl.app.feedback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, JwtAuthenticationConverter JwtAuthenticationConverter) {
        http.cors(c -> c.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/actuator").permitAll()
                        .pathMatchers(HttpMethod.GET, "/actuator/**").permitAll()

                        // RATING
                        .pathMatchers(HttpMethod.GET, "/api/v1/objects/{domainObjectType}/{domainObjectId}/ratings").hasAuthority(SecurityScopes.RATING_READ.getScopeName())
                        .pathMatchers(HttpMethod.GET, "/api/v1/ratings").hasAuthority(SecurityScopes.RATING_READ.getScopeName())
                        .pathMatchers(HttpMethod.GET, "/api/v1/users/{userId}/ratings").hasAuthority(SecurityScopes.RATING_READ.getScopeName())

                        .pathMatchers(HttpMethod.PUT, "/api/v1/ratings").hasAuthority(SecurityScopes.RATING_WRITE.getScopeName())
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/ratings").hasAuthority(SecurityScopes.RATING_WRITE.getScopeName())

                        // REACTION
                        .pathMatchers(HttpMethod.GET, "/api/v1/objects/{domainObjectType}/{domainObjectId}/reactions").hasAuthority(SecurityScopes.REACTION_READ.getScopeName())
                        .pathMatchers(HttpMethod.GET, "/api/v1/reactions").hasAuthority(SecurityScopes.REACTION_READ.getScopeName())
                        .pathMatchers(HttpMethod.GET, "/api/v1/users/{userId}/reactions").hasAuthority(SecurityScopes.REACTION_READ.getScopeName())

                        .pathMatchers(HttpMethod.POST, "/api/v1/reactions").hasAuthority(SecurityScopes.REACTION_WRITE.getScopeName())
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/reactions").hasAuthority(SecurityScopes.REACTION_WRITE.getScopeName())

                        // COMMENT
                        .pathMatchers(HttpMethod.GET, "/api/v1/objects/{domainObjectType}/{domainObjectId}/comments").hasAuthority(SecurityScopes.COMMENT_READ.getScopeName())
                        .pathMatchers(HttpMethod.GET, "/api/v1/comments").hasAuthority(SecurityScopes.COMMENT_READ.getScopeName())

                        .pathMatchers(HttpMethod.POST, "/api/v1/comments").hasAuthority(SecurityScopes.COMMENT_WRITE.getScopeName())
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/comments").hasAuthority(SecurityScopes.COMMENT_WRITE.getScopeName())
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/comments").hasAuthority(SecurityScopes.COMMENT_WRITE.getScopeName())
                        .pathMatchers(HttpMethod.POST, "/api/v1/comments/{commentId}/hides").hasAuthority(SecurityScopes.COMMENT_MODERATE.getScopeName())
                        .pathMatchers(HttpMethod.POST, "/api/v1/comments/{commentId}/restorations").hasAuthority(SecurityScopes.COMMENT_MODERATE.getScopeName())

                        // COMMENT REPORT
                        .pathMatchers(HttpMethod.GET, "/api/v1/reports").hasAuthority(SecurityScopes.COMMENT_REPORT_READ.getScopeName())
                        .pathMatchers(HttpMethod.POST, "/api/v1/comments/{commentId}/reports").hasAuthority(SecurityScopes.COMMENT_REPORT_WRITE.getScopeName())
                        .pathMatchers(HttpMethod.POST, "/api/v1/comments/{commentId}/reports/{reportId}/approvals").hasAuthority(SecurityScopes.COMMENT_REPORT_MANAGE.getScopeName())
                        .pathMatchers(HttpMethod.POST, "/api/v1/comments/{commentId}/reports/{reportId}/rejections").hasAuthority(SecurityScopes.COMMENT_REPORT_MANAGE.getScopeName())

                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(c -> c
                        .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(JwtAuthenticationConverter))));
        return http.build();
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    JwtAuthenticationConverter JwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }

    @Bean
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        return jwtGrantedAuthoritiesConverter;
    }
}
