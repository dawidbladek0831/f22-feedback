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
import pl.app.feedback.reaction.query.model.DomainObjectReaction;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class DomainObjectReactionReadModelSynchronizer {
    private static final Logger logger = LoggerFactory.getLogger(DomainObjectReactionReadModelSynchronizer.class);
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<ReactionEvent.ReactionAddedEvent> handle(ReactionEvent.ReactionAddedEvent event) {
        return updateDomainObjectReaction(event).thenReturn(event);
    }

    public Mono<ReactionEvent.ReactionRemovedEvent> handle(ReactionEvent.ReactionRemovedEvent event) {
        return updateDomainObjectReaction(event).thenReturn(event);
    }

    private Mono<DomainObjectReaction> updateDomainObjectReaction(DomainObjectEvent event) {
        return mongoTemplate.findOne(Query.query(Criteria.where("domainObjectType").is(event.domainObjectType()).and("domainObjectId").is(event.domainObjectId())), DomainObjectReaction.class)
                .defaultIfEmpty(new DomainObjectReaction(event.domainObjectType(), event.domainObjectId()))
                .flatMap(readModel -> {
                    if (event instanceof ReactionEvent.ReactionAddedEvent e) {
                        readModel.handle(e);
                    } else if (event instanceof ReactionEvent.ReactionRemovedEvent e) {
                        readModel.handle(e);
                    }
                    return mongoTemplate.save(readModel);
                })
                .doOnSuccess(obj -> logger.debug("updated DomainObjectReaction read model: {}-{}", event.domainObjectType(), event.domainObjectId()))
                .doOnError(e -> logger.error("error updating DomainObjectReaction read model: {}-{}, {}", event.domainObjectType(), event.domainObjectId(), e.toString()));
    }
}
