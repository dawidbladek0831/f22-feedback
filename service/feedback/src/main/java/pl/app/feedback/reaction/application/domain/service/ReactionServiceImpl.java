package pl.app.feedback.reaction.application.domain.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import pl.app.common.event.EventPublisher;
import pl.app.feedback.reaction.application.domain.model.ReactionEvent;
import pl.app.feedback.reaction.application.domain.model.Reaction;
import pl.app.feedback.reaction.application.port.in.ReactionCommand;
import pl.app.feedback.reaction.application.port.in.ReactionService;
import pl.app.feedback.reaction.application.port.out.ReactionDomainRepository;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class ReactionServiceImpl implements ReactionService {

    private static final Logger logger = LoggerFactory.getLogger(ReactionServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final EventPublisher eventPublisher;
    private final ReactionDomainRepository repository;

    private final AllowedDomainObjectTypesPolicy allowedDomainObjectTypesPolicy;
    private final AllowedReactionsPolicy allowedReactionsPolicy;
    private final ReactionCreationPolicy reactionCreationPolicy;
    private final SingleReactionPolicy singleReactionPolicy;
    private final LikeDislikePolicy likeDislikePolicy;


    @Override
    public Mono<Reaction> add(ReactionCommand.AddReactionCommand rawCommand) {
        var command = normalizeCommand(rawCommand);
        return Mono.fromCallable(() ->
                allowedDomainObjectTypesPolicy.apply(command.domainObjectType())
                        .then(allowedReactionsPolicy.apply(command.domainObjectType(), command.reaction()))
                        .then(reactionCreationPolicy.apply(command.domainObjectType(), command.domainObjectId(), command.userId())
                                .flatMap(domain -> singleReactionPolicy.apply(domain))
                                .flatMap(domain -> likeDislikePolicy.apply(domain, command.reaction()))
                                .flatMap(domain -> {
                                    domain.addReaction(command.reaction());
                                    return mongoTemplate.save(domain)
                                            .then(eventPublisher.publish(new ReactionEvent.ReactionAddedEvent(domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId(), domain.getUserId(), command.reaction())))
                                            .thenReturn(domain);
                                }))

        ).doOnSubscribe(subscription ->
                logger.debug("adding reaction: {}-{}-{} {}", command.domainObjectType(), command.domainObjectId(), command.userId(), command.reaction())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("added reaction: {}-{}-{} {}", command.domainObjectType(), command.domainObjectId(), command.userId(), command.reaction())
        ).doOnError(e ->
                logger.error("exception occurred while adding reaction: {}-{}-{} {}, exception: {}", command.domainObjectType(), command.domainObjectId(), command.userId(), command.reaction(), e.toString())
        );
    }

    private ReactionCommand.AddReactionCommand normalizeCommand(ReactionCommand.AddReactionCommand command) {
        return new ReactionCommand.AddReactionCommand(
                command.domainObjectType().toUpperCase(),
                command.domainObjectId(),
                command.userId(),
                command.reaction().toUpperCase()
        );
    }

    @Override
    public Mono<Reaction> remove(ReactionCommand.RemoveReactionCommand rawCommand) {
        var command = normalizeCommand(rawCommand);
        return Mono.fromCallable(() ->
                repository.fetchByDomainObjectAndUser(command.domainObjectType(), command.domainObjectId(), command.userId())
                        .flatMap(domain -> {
                            domain.removeReaction(command.reaction());
                            return mongoTemplate.save(domain)
                                    .then(eventPublisher.publish(new ReactionEvent.ReactionRemovedEvent(domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId(), domain.getUserId(), command.reaction())))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("removing reaction: {}-{}-{} {}", command.domainObjectType(), command.domainObjectId(), command.userId(), command.reaction())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("removed reaction: {}-{}-{} {}", command.domainObjectType(), command.domainObjectId(), command.userId(), command.reaction())
        ).doOnError(e ->
                logger.error("exception occurred while removing reaction: {}-{}-{} {}, exception: {}", command.domainObjectType(), command.domainObjectId(), command.userId(), command.reaction(), e.toString())
        );
    }

    private ReactionCommand.RemoveReactionCommand normalizeCommand(ReactionCommand.RemoveReactionCommand command) {
        return new ReactionCommand.RemoveReactionCommand(
                command.domainObjectType().toUpperCase(),
                command.domainObjectId(),
                command.userId(),
                command.reaction().toUpperCase()
        );
    }

}
