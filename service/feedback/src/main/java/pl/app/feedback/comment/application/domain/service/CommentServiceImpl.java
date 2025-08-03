package pl.app.feedback.comment.application.domain.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import pl.app.common.event.EventPublisher;
import pl.app.feedback.comment.application.domain.model.Comment;
import pl.app.feedback.comment.application.domain.model.CommentEvent;
import pl.app.feedback.comment.application.port.in.CommentCommand;
import pl.app.feedback.comment.application.port.in.CommentService;
import pl.app.feedback.comment.application.port.out.CommentDomainRepository;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class CommentServiceImpl implements CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
    private final ReactiveMongoTemplate mongoTemplate;
    private final EventPublisher eventPublisher;
    private final CommentDomainRepository repository;

    @Override
    public Mono<Comment> create(CommentCommand.CreateCommentCommand command) {
        return Mono.fromCallable(() -> {
                    var domain = new Comment(command.domainObjectType(), command.domainObjectId(), command.userId(), command.content(), command.parentId());
                    return mongoTemplate.insert(domain)
                            .then(eventPublisher.publish(new CommentEvent.CommentCreatedEvent(
                                    domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId(), domain.getUserId(), domain.getContent(), domain.getParentId()
                            )))
                            .thenReturn(domain);
                }
        ).doOnSubscribe(subscription ->
                logger.debug("creating comment to domain object: {}-{}", command.domainObjectType(), command.domainObjectId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created comment: {}, to domain object: {}-{}", domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId())
        ).doOnError(e ->
                logger.error("exception occurred while creating comment to domain object: {}-{}, exception: {}", command.domainObjectType(), command.domainObjectId(), e.toString())
        );
    }

    @Override
    public Mono<Comment> update(CommentCommand.UpdateCommentCommand command) {
        return Mono.fromCallable(() -> repository.fetch(command.commentId())
                .flatMap(domain -> {
                    domain.updateContent(command.content());
                    return mongoTemplate.save(domain)
                            .then(eventPublisher.publish(new CommentEvent.CommentUpdatedEvent(
                                    domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId(), domain.getContent()
                            )))
                            .thenReturn(domain);
                })
        ).doOnSubscribe(subscription ->
                logger.debug("updating comment: {}", command.commentId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("updated comment: {}, to domain object: {}-{}", domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId())
        ).doOnError(e ->
                logger.error("exception occurred while updating comment: {}, exception: {}", command.commentId(), e.toString())
        );
    }

    @Override
    public Mono<Comment> remove(CommentCommand.RemoveCommentCommand command) {
        return Mono.fromCallable(() -> repository.fetch(command.commentId())
                .flatMap(domain -> {
                    domain.markObjectAsRemoved();
                    return mongoTemplate.save(domain)
                            .then(eventPublisher.publish(new CommentEvent.CommentRemovedEvent(
                                    domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId()
                            )))
                            .thenReturn(domain);
                })
        ).doOnSubscribe(subscription ->
                logger.debug("removing comment: {}", command.commentId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("removed comment: {}, to domain object: {}-{}", domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId())
        ).doOnError(e ->
                logger.error("exception occurred while removing comment: {}, exception: {}", command.commentId(), e.toString())
        );
    }

    @Override
    public Mono<Comment> hide(CommentCommand.HideCommentCommand command) {
        return Mono.fromCallable(() -> repository.fetch(command.commentId())
                .flatMap(domain -> {
                    domain.hide();
                    return mongoTemplate.save(domain)
                            .then(eventPublisher.publish(new CommentEvent.CommentHiddenEvent(
                                    domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId()
                            )))
                            .thenReturn(domain);
                })
        ).doOnSubscribe(subscription ->
                logger.debug("hiding comment: {}", command.commentId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("hidden comment: {}, to domain object: {}-{}", domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId())
        ).doOnError(e ->
                logger.error("exception occurred while hiding comment: {}, exception: {}", command.commentId(), e.toString())
        );
    }

    @Override
    public Mono<Comment> restore(CommentCommand.RestoreCommentCommand command) {
        return Mono.fromCallable(() -> repository.fetch(command.commentId())
                .flatMap(domain -> {
                    domain.restore();
                    return mongoTemplate.save(domain)
                            .then(eventPublisher.publish(new CommentEvent.CommentRestoredEvent(
                                    domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId()
                            )))
                            .thenReturn(domain);
                })
        ).doOnSubscribe(subscription ->
                logger.debug("restoring comment: {}", command.commentId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("restored comment: {}, to domain object: {}-{}", domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId())
        ).doOnError(e ->
                logger.error("exception occurred while restoring comment: {}, exception: {}", command.commentId(), e.toString())
        );
    }
}
