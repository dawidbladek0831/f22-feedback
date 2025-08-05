package pl.app.feedback.rating.application.domain.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import pl.app.common.event.EventPublisher;
import pl.app.feedback.rating.application.domain.model.Rating;
import pl.app.feedback.rating.application.domain.model.RatingEvent;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class UpdatingRatingPolicy {
    private static final Logger logger = LoggerFactory.getLogger(UpdatingRatingPolicy.class);

    private final EventPublisher eventPublisher;
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<Rating> apply(Rating domain, Double newRating) {
        return Mono.fromCallable(() -> {
                    var oldRating = domain.setNewRating(newRating);
                    return mongoTemplate.save(domain)
                            .then(eventPublisher.publish(new RatingEvent.RatingUpdatedEvent(domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId(), domain.getUserId(), newRating, oldRating)))
                            .thenReturn(domain);
                }
        ).doOnSubscribe(subscription ->
                logger.debug("updating rating: {}-{}-{}", domain.getDomainObjectType(), domain.getDomainObjectId(), domain.getRating())
        ).flatMap(Function.identity()).doOnSuccess(d ->
                logger.debug("updated rating: {}-{}-{}", domain.getDomainObjectType(), domain.getDomainObjectId(), domain.getRating())
        ).doOnError(e ->
                logger.error("exception occurred while updating rating: {}-{}-{}, exception: {}", domain.getDomainObjectType(), domain.getDomainObjectId(), domain.getRating(), e.toString())
        );
    }
}
