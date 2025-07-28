package pl.app.feedback.reaction.application.domain.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import pl.app.common.event.EventPublisher;
import pl.app.feedback.reaction.application.domain.model.Reaction;
import pl.app.feedback.reaction.application.domain.model.ReactionEvent;
import pl.app.feedback.reaction.application.domain.model.ReactionException;
import pl.app.feedback.reaction.application.port.out.ReactionDomainRepository;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class ReactionCreationPolicy {
    private static final Logger logger = LoggerFactory.getLogger(ReactionCreationPolicy.class);

    private final ReactionDomainRepository repository;
    private final EventPublisher eventPublisher;
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<Reaction> apply(String domainObjectType, String domainObjectId, String userId) {
        return repository.fetchByDomainObjectAndUser(domainObjectType, domainObjectId, userId)
                .onErrorResume(ReactionException.NotFoundReactionException.class, throwable -> create(domainObjectType, domainObjectId, userId));
    }

    private Mono<Reaction> create(String domainObjectType, String domainObjectId, String userId) {
        return Mono.fromCallable(() -> {
                    var domain = new Reaction(domainObjectType, domainObjectId, userId);
                    return mongoTemplate.insert(domain)
                            .then(eventPublisher.publish(new ReactionEvent.ReactionCreatedEvent(domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId(), domain.getUserId())))
                            .thenReturn(domain);
                }

        ).doOnSubscribe(subscription ->
                logger.debug("creating reaction: {}-{}-{}", domainObjectType, domainObjectId, userId)
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created reaction: {}-{}-{}", domainObjectType, domainObjectId, userId)
        ).doOnError(e ->
                logger.error("exception occurred while creating reaction: {}-{}-{}, exception: {}", domainObjectType, domainObjectId, userId, e.toString())
        );
    }
}
