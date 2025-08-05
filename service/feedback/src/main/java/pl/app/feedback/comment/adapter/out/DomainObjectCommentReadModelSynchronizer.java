package pl.app.feedback.comment.adapter.out;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.common.event.DomainObjectEvent;
import pl.app.feedback.comment.application.domain.model.CommentEvent;
import pl.app.feedback.comment.query.model.DomainObjectComment;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class DomainObjectCommentReadModelSynchronizer {
    private static final Logger logger = LoggerFactory.getLogger(DomainObjectCommentReadModelSynchronizer.class);
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<Void> handle(DomainObjectEvent event) {
        return updateDomainObjectComment(event).then();
    }

    private Mono<DomainObjectComment> updateDomainObjectComment(DomainObjectEvent event) {
        return mongoTemplate.findOne(Query.query(Criteria.where("domainObjectType").is(event.domainObjectType()).and("domainObjectId").is(event.domainObjectId())), DomainObjectComment.class)
                .defaultIfEmpty(new DomainObjectComment(event.domainObjectType(), event.domainObjectId()))
                .flatMap(readModel -> {
                    if (event instanceof CommentEvent.CommentCreatedEvent e) {
                        readModel.handle(e);
                    } else if (event instanceof CommentEvent.CommentUpdatedEvent e) {
                        readModel.handle(e);
                    } else if (event instanceof CommentEvent.CommentRemovedEvent e) {
                        readModel.handle(e);
                    } else if (event instanceof CommentEvent.CommentHiddenEvent e) {
                        readModel.handle(e);
                    } else if (event instanceof CommentEvent.CommentRestoredEvent e) {
                        readModel.handle(e);
                    }
                    return mongoTemplate.save(readModel);
                })
                .doOnSuccess(obj -> logger.debug("updated DomainObjectComment read model: {}-{}", event.domainObjectType(), event.domainObjectId()))
                .doOnError(e -> logger.error("error updating DomainObjectComment read model: {}-{}, {}", event.domainObjectType(), event.domainObjectId(), e.toString()));
    }
}
