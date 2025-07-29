package pl.app.feedback.rating.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.feedback.rating.application.domain.model.RatingEvent;
import pl.app.feedback.rating.query.model.DomainObjectRating;
import pl.app.feedback.rating.query.model.UserRating;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class RatingReadModelSynchronizer {
    private static final Logger logger = LoggerFactory.getLogger(RatingReadModelSynchronizer.class);
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<RatingEvent.RatingCreatedEvent> handle(RatingEvent.RatingCreatedEvent event) {
        return Mono.zip(
                updateUserRating(event.userId(), event.id(), event.domainObjectType(), event.domainObjectId(), event.rating(), 0),
                updateDomainObjectRating(event.domainObjectType(), event.domainObjectId(), event.rating(), null, 0)
        ).thenReturn(event);
    }

    public Mono<RatingEvent.RatingUpdatedEvent> handle(RatingEvent.RatingUpdatedEvent event) {
        return Mono.zip(
                updateUserRating(event.userId(), event.id(), event.domainObjectType(), event.domainObjectId(), event.rating(), 1),
                updateDomainObjectRating(event.domainObjectType(), event.domainObjectId(), event.rating(), event.oldRating(), 1)
        ).thenReturn(event);
    }

    public Mono<RatingEvent.RatingRemovedEvent> handle(RatingEvent.RatingRemovedEvent event) {
        return Mono.zip(
                updateUserRating(event.userId(), event.id(), event.domainObjectType(), event.domainObjectId(), event.rating(), 2),
                updateDomainObjectRating(event.domainObjectType(), event.domainObjectId(), event.rating(), null, 2)
        ).thenReturn(event);
    }

    private Mono<UserRating> updateUserRating(String userId, ObjectId ratingId, String domainObjectType, String domainObjectId, Double rating, int mode) {
        return mongoTemplate.findOne(Query.query(Criteria.where("userId").is(userId)), UserRating.class)
                .defaultIfEmpty(new UserRating(userId))
                .flatMap(readModel -> {
                    if (mode == 0) {
                        readModel.getRatings().add(new UserRating.Rating(ratingId, domainObjectType, domainObjectId, userId, rating));
                    } else if (mode == 1) {
                        Optional<UserRating.Rating> any = readModel.getRatings().stream().filter(o -> o.getId().equals(ratingId)).findAny();
                        if (any.isPresent()) {
                            any.get().setRating(rating);
                        } else {
                            readModel.getRatings().add(new UserRating.Rating(ratingId, domainObjectType, domainObjectId, userId, rating));
                        }
                    } else if (mode == 2) {
                        var ratingToRemove = readModel.getRatings().stream()
                                .filter(e -> e.getDomainObjectType().equals(domainObjectType) && e.getDomainObjectId().equals(domainObjectId))
                                .findAny().orElseThrow();
                        readModel.getRatings().remove(ratingToRemove);
                    } else {
                        throw new RuntimeException();
                    }
                    return mongoTemplate.save(readModel);
                })
                .doOnSuccess(obj -> logger.debug("updated UserRating: {}", userId))
                .doOnError(e -> logger.error("error updating UserRating: {}, {}", userId, e.toString()));
    }

    private Mono<DomainObjectRating> updateDomainObjectRating(String domainObjectType, String domainObjectId, Double rating, Double oldRating, int mode) {
        return mongoTemplate.findOne(Query.query(Criteria.where("domainObjectType").is(domainObjectType).and("domainObjectId").is(domainObjectId)), DomainObjectRating.class)
                .defaultIfEmpty(new DomainObjectRating(domainObjectType, domainObjectId))
                .flatMap(readModel -> {
                    if (mode == 0) {
                        readModel.setSum(readModel.getSum() + rating);
                        readModel.setQuantity(readModel.getQuantity() + 1);
                    } else if (mode == 1) {
                        readModel.setSum(readModel.getSum() + rating - oldRating);
                    } else if (mode == 2) {
                        readModel.setSum(readModel.getSum() - rating);
                        readModel.setQuantity(readModel.getQuantity() - 1);
                    } else {
                        throw new RuntimeException();
                    }
                    readModel.calculateRating();
                    return mongoTemplate.save(readModel);
                })
                .doOnSuccess(obj -> logger.debug("updated DomainObjectRating: {}-{}", domainObjectType, domainObjectId))
                .doOnError(e -> logger.error("error updating DomainObjectRating: {}-{}, {}", domainObjectType, domainObjectId, e.toString()));
    }
}
