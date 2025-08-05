package pl.app.feedback.rating.application.port.out;

import pl.app.feedback.rating.application.domain.model.Rating;
import reactor.core.publisher.Mono;

public interface RatingDomainRepository {
    Mono<Rating> fetchByDomainObjectAndUser(String domainObjectType, String domainObjectId, String userId);
}
