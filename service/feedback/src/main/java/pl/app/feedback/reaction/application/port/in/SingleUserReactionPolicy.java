package pl.app.feedback.reaction.application.port.in;

import org.springframework.stereotype.Component;
import pl.app.feedback.reaction.application.domain.UserReaction;
import reactor.core.publisher.Mono;

@Component
class SingleUserReactionPolicy {
    public Mono<UserReaction> apply(UserReaction domain) {
        // TODO
        return Mono.just(domain);
    }
}
