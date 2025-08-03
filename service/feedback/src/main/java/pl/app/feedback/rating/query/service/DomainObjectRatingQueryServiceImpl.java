package pl.app.feedback.rating.query.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.feedback.rating.application.domain.model.Rating;
import pl.app.feedback.rating.application.domain.model.RatingException;
import pl.app.feedback.rating.query.dto.RatingDto;
import pl.app.feedback.rating.query.model.DomainObjectRating;
import pl.app.feedback.rating.query.model.UserRating;
import pl.app.feedback.rating.query.port.DomainObjectRatingQueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RequiredArgsConstructor
class DomainObjectRatingQueryServiceImpl implements DomainObjectRatingQueryService {
    private final ReactiveMongoTemplate mongoTemplate;
    private final RatingMapper mapper;

    @Override
    public Mono<DomainObjectRating> fetchDomainObjectRating(String domainObjectType, String domainObjectId) {
        return mongoTemplate.query(DomainObjectRating.class)
                .matching(Query.query(Criteria
                        .where("domainObjectType").is(domainObjectType)
                        .and("domainObjectId").is(domainObjectId)
                )).one()
                .defaultIfEmpty(new DomainObjectRating(domainObjectType, domainObjectId));
    }

    @Override
    public Flux<RatingDto> fetchUserRating(String userId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("userId").is(userId)), UserRating.class)
                .defaultIfEmpty(new UserRating(userId))
                .map(UserRating::getRatings)
                .flatMapMany(Flux::fromIterable)
                .map(r -> mapper.map(r, RatingDto.class));
    }

    @Override
    public Mono<RatingDto> fetchBy(String userId, String domainObjectType, String domainObjectId) {
        return mongoTemplate.query(Rating.class)
                .matching(Query.query(Criteria
                        .where("domainObjectType").is(domainObjectType)
                        .and("domainObjectId").is(domainObjectId)
                        .and("userId").is(userId)
                )).one()
                .map(e -> mapper.map(e, RatingDto.class))
                .switchIfEmpty(Mono.error(RatingException.NotFoundRatingException::new));
    }

    @Override
    public Flux<RatingDto> fetchAllBy(String userId, String domainObjectType, String domainObjectId, String cursor, Integer pageSize) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(userId)) {
            criteria = criteria.and("userId").is(userId);
        }
        if (Objects.nonNull(domainObjectType)) {
            criteria = criteria.and("domainObjectType").is(domainObjectType);
        }
        if (Objects.nonNull(domainObjectId)) {
            criteria = criteria.and("domainObjectId").is(domainObjectId);
        }
        if (Objects.nonNull(cursor)) {
            if (ObjectId.isValid(cursor)) {
                criteria = criteria.and("_id").gt(new ObjectId(cursor));
            }
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 50;
        }
        Query query = Query.query(criteria).limit(pageSize)
                .with(Sort.by(Sort.Direction.ASC, "_id"));
        return mongoTemplate.query(Rating.class)
                .matching(query)
                .all()
                .map(e -> mapper.map(e, RatingDto.class));
    }
}
