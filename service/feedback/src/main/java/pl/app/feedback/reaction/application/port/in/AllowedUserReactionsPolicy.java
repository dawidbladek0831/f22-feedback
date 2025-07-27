package pl.app.feedback.reaction.application.port.in;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
class AllowedUserReactionsPolicy {
    public Mono<Void> apply(String domainObjectType, String reaction) {
        // TODO
        return Mono.empty();
    }
}
