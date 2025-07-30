package pl.app.feedback.reaction.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import pl.app.feedback.reaction.application.domain.model.ReactionEvent;
import pl.app.feedback.reaction.query.model.DomainObjectReaction;
import pl.app.feedback.reaction.query.model.UserReaction;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class ReadModelSynchronizer {
    private static final Logger logger = LoggerFactory.getLogger(ReadModelSynchronizer.class);
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<ReactionEvent.ReactionAddedEvent> handle(ReactionEvent.ReactionAddedEvent event) {
        return Mono.zip(
                        mongoTemplate.findOne(Query.query(Criteria.where("domainObjectType").is(event.domainObjectType()).and("domainObjectId").is(event.domainObjectId())), DomainObjectReaction.class)
                                .defaultIfEmpty(new DomainObjectReaction(event.domainObjectType(), event.domainObjectId()))
                                .flatMap(readModel -> {
                                    readModel.handle(event);
                                    return mongoTemplate.save(readModel);
                                }),
                        mongoTemplate.findOne(Query.query(Criteria.where("userId").is(event.userId())), UserReaction.class)
                                .defaultIfEmpty(new UserReaction(event.userId()))
                                .flatMap(readModel -> {
                                    readModel.handle(event);
                                    return mongoTemplate.save(readModel);
                                })
                )
                .doOnSuccess(obj -> logger.debug("updated Reaction read models: {}-{}", event.domainObjectType(), event.domainObjectId()))
                .doOnError(e -> logger.error("error updating Reaction read models: {}-{}, {}", event.domainObjectType(), event.domainObjectId(), e.toString()))
                .thenReturn(event);
    }

    public Mono<ReactionEvent.ReactionRemovedEvent> handle(ReactionEvent.ReactionRemovedEvent event) {
        return Mono.zip(
                        mongoTemplate.findOne(Query.query(Criteria.where("domainObjectType").is(event.domainObjectType()).and("domainObjectId").is(event.domainObjectId())), DomainObjectReaction.class)
                                .defaultIfEmpty(new DomainObjectReaction(event.domainObjectType(), event.domainObjectId()))
                                .flatMap(readModel -> {
                                    readModel.handle(event);
                                    return mongoTemplate.save(readModel);
                                }),
                        mongoTemplate.findOne(Query.query(Criteria.where("userId").is(event.userId())), UserReaction.class)
                                .defaultIfEmpty(new UserReaction(event.userId()))
                                .flatMap(readModel -> {
                                    readModel.handle(event);
                                    return mongoTemplate.save(readModel);
                                })
                )
                .doOnSuccess(obj -> logger.debug("updated Reaction read models: {}-{}", event.domainObjectType(), event.domainObjectId()))
                .doOnError(e -> logger.error("error updating Reaction read models: {}-{}, {}", event.domainObjectType(), event.domainObjectId(), e.toString()))
                .thenReturn(event);
    }
}
