package pl.app.feedback.reaction.application.port.in;

import pl.app.feedback.reaction.application.domain.UserReaction;
import reactor.core.publisher.Mono;

interface UserReactionDomainRepository {
    Mono<UserReaction> fetchByDomainObjectAndUser(String domainObjectType, String domainObjectId, String userId);
}
