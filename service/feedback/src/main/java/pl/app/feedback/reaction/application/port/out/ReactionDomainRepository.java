package pl.app.feedback.reaction.application.port.out;

import pl.app.feedback.reaction.application.domain.model.Reaction;
import reactor.core.publisher.Mono;

public interface ReactionDomainRepository {
    Mono<Reaction> fetchByDomainObjectAndUser(String domainObjectType, String domainObjectId, String userId);
}
