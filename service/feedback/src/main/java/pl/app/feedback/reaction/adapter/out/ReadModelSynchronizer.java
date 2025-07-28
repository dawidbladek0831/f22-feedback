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

import java.util.Optional;

@Component
@RequiredArgsConstructor
class ReadModelSynchronizer {
    private static final Logger logger = LoggerFactory.getLogger(ReadModelSynchronizer.class);
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<ReactionEvent.ReactionAddedEvent> handle(ReactionEvent.ReactionAddedEvent event) {
        updateDomainObjectReaction(event.domainObjectType(), event.domainObjectId(), event.reaction(), 1).subscribe();
        updateUserReaction(event.userId(), event.domainObjectType(), event.domainObjectId(), event.reaction(), true).subscribe();
        return Mono.just(event);
    }

    public Mono<ReactionEvent.ReactionRemovedEvent> handle(ReactionEvent.ReactionRemovedEvent event) {
        updateDomainObjectReaction(event.domainObjectType(), event.domainObjectId(), event.reaction(), -1).subscribe();
        updateUserReaction(event.userId(), event.domainObjectType(), event.domainObjectId(), event.reaction(), false).subscribe();
        return Mono.just(event);
    }

    private Mono<DomainObjectReaction> updateDomainObjectReaction(String domainObjectType, String domainObjectId, String reaction, int delta) {
        Query query = Query.query(Criteria.where("domainObjectType").is(domainObjectType).and("domainObjectId").is(domainObjectId));
        Update update = new Update().inc("reactions." + reaction, delta)
                .setOnInsert("id", ObjectId.get())
                .setOnInsert("domainObjectType", domainObjectType)
                .setOnInsert("domainObjectId", domainObjectId);

        return mongoTemplate.findAndModify(
                        query,
                        update,
                        FindAndModifyOptions.options().returnNew(true).upsert(true),
                        DomainObjectReaction.class
                )
                .doOnSuccess(obj -> logger.debug("updated DomainObjectReaction: {}-{}", domainObjectType, domainObjectId))
                .doOnError(e -> logger.error("error updating DomainObjectReaction: {}-{}, {}", domainObjectType, domainObjectId, e.toString()));
    }

    private Mono<UserReaction> updateUserReaction(String userId, String domainObjectType, String domainObjectId, String reactionType, boolean add) {
        return mongoTemplate.findOne(Query.query(Criteria.where("userId").is(userId)), UserReaction.class)
                .defaultIfEmpty(new UserReaction(userId))
                .flatMap(userReaction -> {
                    Optional<UserReaction.Reaction> optional = userReaction.getReactions().stream()
                            .filter(r -> r.getDomainObjectType().equals(domainObjectType) && r.getDomainObjectId().equals(domainObjectId))
                            .findFirst();
                    if (optional.isPresent()) {
                        if (add) {
                            optional.get().getReactions().add(reactionType);
                        } else {
                            optional.get().getReactions().remove(reactionType);
                        }
                    } else {
                        var reaction = new UserReaction.Reaction(
                                domainObjectType,
                                domainObjectId,
                                userId
                        );
                        if (add) {
                            reaction.getReactions().add(reactionType);
                        }
                        userReaction.getReactions().add(reaction);
                    }
                    return mongoTemplate.save(userReaction);
                })
                .doOnSuccess(obj -> logger.debug("updated UserReaction: {}-{}", domainObjectType, domainObjectId))
                .doOnError(e -> logger.error("error updating UserReaction: {}-{}, {}", domainObjectType, domainObjectId, e.toString()));
    }

}
