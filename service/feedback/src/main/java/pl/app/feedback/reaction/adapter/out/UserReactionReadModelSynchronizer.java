package pl.app.feedback.reaction.adapter.out;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.common.event.DomainObjectEvent;
import pl.app.feedback.reaction.application.domain.model.ReactionEvent;
import pl.app.feedback.reaction.query.model.UserReaction;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class UserReactionReadModelSynchronizer {
    private static final Logger logger = LoggerFactory.getLogger(UserReactionReadModelSynchronizer.class);
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<ReactionEvent.ReactionAddedEvent> handle(ReactionEvent.ReactionAddedEvent event) {
        return updateUserReaction(event).thenReturn(event);
    }

    public Mono<ReactionEvent.ReactionRemovedEvent> handle(ReactionEvent.ReactionRemovedEvent event) {
        return updateUserReaction(event).thenReturn(event);
    }

    private Mono<UserReaction> updateUserReaction(DomainObjectEvent event) {
        return mongoTemplate.findOne(Query.query(Criteria.where("userId").is(getUserId(event))), UserReaction.class)
                .defaultIfEmpty(new UserReaction(getUserId(event)))
                .flatMap(readModel -> {
                    if (event instanceof ReactionEvent.ReactionAddedEvent e) {
                        readModel.handle(e);
                    } else if (event instanceof ReactionEvent.ReactionRemovedEvent e) {
                        readModel.handle(e);
                    }
                    return mongoTemplate.save(readModel);
                })
                .doOnSuccess(obj -> logger.debug("updated UserReaction read model: {}-{}", event.domainObjectType(), event.domainObjectId()))
                .doOnError(e -> logger.error("error updating UserReaction read model: {}-{}, {}", event.domainObjectType(), event.domainObjectId(), e.toString()));
    }

    private String getUserId(DomainObjectEvent event) {
        if (event instanceof ReactionEvent.ReactionAddedEvent e) {
            return e.userId();
        } else if (event instanceof ReactionEvent.ReactionRemovedEvent e) {
            return e.userId();
        }
        throw new RuntimeException();
    }
}
