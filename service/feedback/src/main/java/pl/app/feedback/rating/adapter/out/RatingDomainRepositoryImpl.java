package pl.app.feedback.rating.adapter.out;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.feedback.rating.application.domain.model.Rating;
import pl.app.feedback.rating.application.domain.model.RatingException;
import pl.app.feedback.rating.application.port.out.RatingDomainRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class RatingDomainRepositoryImpl implements RatingDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Rating> fetchByDomainObjectAndUser(String domainObjectType, String domainObjectId, String userId) {
        return mongoTemplate.query(Rating.class)
                .matching(Query.query(Criteria
                        .where("domainObjectType").is(domainObjectType)
                        .and("domainObjectId").is(domainObjectId)
                        .and("userId").is(userId)
                )).one()
                .switchIfEmpty(Mono.error(RatingException.NotFoundRatingException::new));
    }
}
