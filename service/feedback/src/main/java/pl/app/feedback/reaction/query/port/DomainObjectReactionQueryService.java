package pl.app.feedback.reaction.query.port;

import pl.app.feedback.reaction.query.dto.DomainObjectReactionDto;
import pl.app.feedback.reaction.query.dto.ReactionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DomainObjectReactionQueryService {
    Mono<DomainObjectReactionDto> fetchDomainObjectReaction(String domainObjectType, String domainObjectId);

    Flux<ReactionDto> fetchAllUserReaction(String userId);

    Mono<ReactionDto> fetchBy(String userId, String domainObjectType, String domainObjectId);

    Flux<ReactionDto> fetchAllBy(String userId, String domainObjectType, String domainObjectId, String cursor, Integer pageSize);
}
