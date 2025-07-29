package pl.app.feedback.rating.application.port.out;

import pl.app.feedback.rating.application.domain.model.Rating;
import pl.app.feedback.reaction.application.domain.model.Reaction;
import reactor.core.publisher.Mono;

public interface RatingDomainRepository {
    Mono<Rating> fetchByDomainObjectAndUser(String domainObjectType, String domainObjectId, String userId);
}
