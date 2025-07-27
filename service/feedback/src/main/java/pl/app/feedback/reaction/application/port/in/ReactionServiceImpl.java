package pl.app.feedback.reaction.application.port.in;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import pl.app.common.event.EventPublisher;
import pl.app.feedback.reaction.application.domain.ReactionEvent;
import pl.app.feedback.reaction.application.domain.UserReaction;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class ReactionServiceImpl implements ReactionService {

    private static final Logger logger = LoggerFactory.getLogger(ReactionServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final EventPublisher eventPublisher;

    private final AllowedDomainObjectTypesPolicy allowedDomainObjectTypesPolicy;
    private final AllowedUserReactionsPolicy allowedUserReactionsPolicy;
    private final UserReactionCreationPolicy userReactionCreationPolicy;
    private final SingleUserReactionPolicy singleUserReactionPolicy;
    private final LikeDislikePolicy likeDislikePolicy;


    @Override
    public Mono<UserReaction> add(ReactionCommand.AddUserReactionCommand rawCommand) {
        var command = normalizeCommand(rawCommand);
        return Mono.fromCallable(() ->
            allowedDomainObjectTypesPolicy.apply(command.domainObjectType())
                    .then(allowedUserReactionsPolicy.apply(command.domainObjectType(), command.reaction()))
                    .then(userReactionCreationPolicy.apply(command.domainObjectType(), command.domainObjectId(), command.userId())
                            .flatMap(domain -> singleUserReactionPolicy.apply(domain))
                            .flatMap(domain -> likeDislikePolicy.apply(domain, command.reaction()))
                            .flatMap(domain -> {
                                domain.addReaction(command.reaction());
                                return mongoTemplate.save(domain)
                                        .then(eventPublisher.publish(new ReactionEvent.UserReactionAddedEvent(domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId(), domain.getUserId(), command.reaction())))
                                        .thenReturn(domain);
                            }))

        ).doOnSubscribe(subscription ->
                logger.debug("adding user reaction: {}-{}-{} {}", command.domainObjectType(), command.domainObjectId(), command.userId(), command.reaction())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("added user reaction: {}-{}-{} {}", command.domainObjectType(), command.domainObjectId(), command.userId(), command.reaction())
        ).doOnError(e ->
                logger.error("exception occurred while adding user reaction: {}-{}-{} {}, exception: {}", command.domainObjectType(), command.domainObjectId(), command.userId(), command.reaction(), e.toString())
        );
    }

    private ReactionCommand.AddUserReactionCommand normalizeCommand(ReactionCommand.AddUserReactionCommand command) {
        return new ReactionCommand.AddUserReactionCommand(
                command.domainObjectType().toUpperCase(),
                command.domainObjectId(),
                command.userId(),
                command.reaction().toUpperCase()
        );
    }

    @Override
    public Mono<UserReaction> remove(ReactionCommand.RemoveUserReactionCommand command) {
        return null;
    }


}
