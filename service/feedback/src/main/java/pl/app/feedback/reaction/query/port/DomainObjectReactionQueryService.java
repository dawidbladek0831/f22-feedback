package pl.app.feedback.reaction.query.port;

import pl.app.feedback.reaction.application.domain.model.Reaction;
import pl.app.feedback.reaction.query.model.DomainObjectReaction;
import pl.app.feedback.reaction.query.model.UserReaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DomainObjectReactionQueryService {
    Flux<DomainObjectReaction> fetchAll();

    Mono<DomainObjectReaction> fetchBy(String domainObjectType, String domainObjectId);

    Mono<Reaction> fetchBy(String userId, String domainObjectType, String domainObjectId);

    Mono<UserReaction> fetchBy(String userId);
}
