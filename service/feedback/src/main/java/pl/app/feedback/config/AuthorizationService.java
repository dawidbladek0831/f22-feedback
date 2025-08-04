package pl.app.feedback.config;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;
import org.springframework.stereotype.Component;
import pl.app.common.exception.AuthorizationException;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class AuthorizationService {
    public static Mono<String> subjectId() {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(AuthorizationException::new))
                .map(ctx -> ctx.getAuthentication().getPrincipal())
                .cast(Jwt.class)
                .map(JwtClaimAccessor::getSubject);
    }

    public static Mono<Void> verifySubjectIsOwner(String subjectId) {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(AuthorizationException::new))
                .map(ctx -> ctx.getAuthentication().getPrincipal())
                .cast(Jwt.class)
                .flatMap(jwt -> {
                    String id = jwt.getSubject();
                    if (Objects.isNull(subjectId) || Objects.isNull(id)) {
                        return Mono.error(AuthorizationException::new);
                    }
                    return subjectId.equals(id) ? Mono.empty() : Mono.error(AuthorizationException::new);
                });
    }

    public static Mono<Void> verifySubjectHasAuthority(String authority) {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(AuthorizationException::new))
                .flatMap(context -> context.getAuthentication().getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals(authority)) ? Mono.empty() : Mono.error(AuthorizationException::new));
    }

    public static Mono<Void> verifySubjectIsOwnerOrHasAuthority(String subjectId, String authority) {
        return verifySubjectIsOwner(subjectId)
                .onErrorResume(AuthorizationException.class, e -> verifySubjectHasAuthority(authority));
    }
}
