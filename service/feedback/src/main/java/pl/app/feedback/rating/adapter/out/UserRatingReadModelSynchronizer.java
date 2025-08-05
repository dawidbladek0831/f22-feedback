package pl.app.feedback.rating.adapter.out;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.common.event.DomainObjectEvent;
import pl.app.feedback.rating.application.domain.model.RatingEvent;
import pl.app.feedback.rating.query.model.UserRating;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class UserRatingReadModelSynchronizer {
    private static final Logger logger = LoggerFactory.getLogger(UserRatingReadModelSynchronizer.class);
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<RatingEvent.RatingCreatedEvent> handle(RatingEvent.RatingCreatedEvent event) {
        return updateUserRating(event).thenReturn(event);
    }

    public Mono<RatingEvent.RatingUpdatedEvent> handle(RatingEvent.RatingUpdatedEvent event) {
        return updateUserRating(event).thenReturn(event);
    }

    public Mono<RatingEvent.RatingRemovedEvent> handle(RatingEvent.RatingRemovedEvent event) {
        return updateUserRating(event).thenReturn(event);
    }

    private Mono<UserRating> updateUserRating(DomainObjectEvent event) {
        return mongoTemplate.findOne(Query.query(Criteria.where("userId").is(getUserId(event))), UserRating.class)
                .defaultIfEmpty(new UserRating(getUserId(event)))
                .flatMap(readModel -> {
                    if (event instanceof RatingEvent.RatingCreatedEvent e) {
                        readModel.handle(e);
                    } else if (event instanceof RatingEvent.RatingUpdatedEvent e) {
                        readModel.handle(e);
                    } else if (event instanceof RatingEvent.RatingRemovedEvent e) {
                        readModel.handle(e);
                    }
                    return mongoTemplate.save(readModel);
                })
                .doOnSuccess(obj -> logger.debug("updated UserRating read model: {}-{}", event.domainObjectType(), event.domainObjectId()))
                .doOnError(e -> logger.error("error updating UserRating read model: {}-{}, {}", event.domainObjectType(), event.domainObjectId(), e.toString()));
    }

    private String getUserId(DomainObjectEvent event) {
        if (event instanceof RatingEvent.RatingCreatedEvent e) {
            return e.userId();
        } else if (event instanceof RatingEvent.RatingUpdatedEvent e) {
            return e.userId();
        } else if (event instanceof RatingEvent.RatingRemovedEvent e) {
            return e.userId();
        }
        throw new RuntimeException();
    }
}
