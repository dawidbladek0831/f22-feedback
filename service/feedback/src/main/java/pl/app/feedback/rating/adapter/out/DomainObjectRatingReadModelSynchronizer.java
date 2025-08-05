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
import pl.app.feedback.rating.query.model.DomainObjectRating;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class DomainObjectRatingReadModelSynchronizer {
    private static final Logger logger = LoggerFactory.getLogger(DomainObjectRatingReadModelSynchronizer.class);
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<RatingEvent.RatingCreatedEvent> handle(RatingEvent.RatingCreatedEvent event) {
        return updateDomainObjectRating(event).thenReturn(event);
    }

    public Mono<RatingEvent.RatingUpdatedEvent> handle(RatingEvent.RatingUpdatedEvent event) {
        return updateDomainObjectRating(event).thenReturn(event);
    }

    public Mono<RatingEvent.RatingRemovedEvent> handle(RatingEvent.RatingRemovedEvent event) {
        return updateDomainObjectRating(event).thenReturn(event);
    }

    private Mono<DomainObjectRating> updateDomainObjectRating(DomainObjectEvent event) {
        return mongoTemplate.findOne(Query.query(Criteria.where("domainObjectType").is(event.domainObjectType()).and("domainObjectId").is(event.domainObjectId())), DomainObjectRating.class)
                .defaultIfEmpty(new DomainObjectRating(event.domainObjectType(), event.domainObjectId()))
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
                .doOnSuccess(obj -> logger.debug("updated DomainObjectRating read model: {}-{}", event.domainObjectType(), event.domainObjectId()))
                .doOnError(e -> logger.error("error updating DomainObjectRating read model: {}-{}, {}", event.domainObjectType(), event.domainObjectId(), e.toString()));
    }
}
