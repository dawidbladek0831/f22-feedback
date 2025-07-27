package pl.app.feedback.reaction.application.port.in;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import pl.app.common.event.EventPublisher;
import pl.app.feedback.reaction.application.domain.ReactionEvent;
import pl.app.feedback.reaction.application.domain.ReactionException;
import pl.app.feedback.reaction.application.domain.UserReaction;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class UserReactionCreationPolicy {
    private static final Logger logger = LoggerFactory.getLogger(UserReactionCreationPolicy.class);

    private final UserReactionDomainRepository repository;
    private final EventPublisher eventPublisher;
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<UserReaction> apply(String domainObjectType, String domainObjectId, String userId) {
        return repository.fetchByDomainObjectAndUser(domainObjectType, domainObjectId, userId)
                .onErrorResume(ReactionException.NotFoundUserReactionException.class, throwable -> create(domainObjectType, domainObjectId, userId));
    }

    private Mono<UserReaction> create(String domainObjectType, String domainObjectId, String userId) {
        return Mono.fromCallable(() ->{
            var domain = new UserReaction(domainObjectType, domainObjectId, userId);
            return mongoTemplate.insert(domain)
                    .then(eventPublisher.publish(new ReactionEvent.UserReactionCreatedEvent(domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId(), domain.getUserId())))
                    .thenReturn(domain);
                }

        ).doOnSubscribe(subscription ->
                logger.debug("creating user reaction: {}-{}-{}", domainObjectType, domainObjectId, userId)
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created user reaction: {}-{}-{}", domainObjectType, domainObjectId, userId)
        ).doOnError(e ->
                logger.error("exception occurred while creating user reaction: {}-{}-{}, exception: {}", domainObjectType, domainObjectId, userId, e.toString())
        );
    }
}
