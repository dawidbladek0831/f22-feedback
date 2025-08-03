package pl.app.feedback.rating.query.port;

import pl.app.feedback.rating.query.dto.RatingDto;
import pl.app.feedback.rating.query.model.DomainObjectRating;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DomainObjectRatingQueryService {
    Mono<DomainObjectRating> fetchDomainObjectRating(String domainObjectType, String domainObjectId);

    Flux<RatingDto> fetchUserRating(String userId);

    Mono<RatingDto> fetchBy(String userId, String domainObjectType, String domainObjectId);

    Flux<RatingDto> fetchAllBy(String userId, String domainObjectType, String domainObjectId, String cursor, Integer pageSize);
}
