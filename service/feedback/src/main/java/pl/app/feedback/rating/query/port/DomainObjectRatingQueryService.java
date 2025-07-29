package pl.app.feedback.rating.query.port;

import pl.app.feedback.rating.application.domain.model.Rating;
import pl.app.feedback.rating.query.model.DomainObjectRating;
import pl.app.feedback.rating.query.model.UserRating;
import reactor.core.publisher.Mono;

public interface DomainObjectRatingQueryService {
    Mono<DomainObjectRating> fetchBy(String domainObjectType, String domainObjectId);

    Mono<Rating> fetchBy(String userId, String domainObjectType, String domainObjectId);

    Mono<UserRating> fetchBy(String userId);
}
