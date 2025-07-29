package pl.app.feedback.rating.application.domain.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import pl.app.common.event.EventPublisher;
import pl.app.feedback.rating.application.domain.model.Rating;
import pl.app.feedback.rating.application.domain.model.RatingEvent;
import pl.app.feedback.rating.application.domain.model.RatingException;
import pl.app.feedback.rating.application.port.in.RatingCommand;
import pl.app.feedback.rating.application.port.in.RatingService;
import pl.app.feedback.rating.application.port.out.RatingDomainRepository;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class RatingServiceImpl implements RatingService {
    private static final Logger logger = LoggerFactory.getLogger(RatingServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final EventPublisher eventPublisher;
    private final RatingDomainRepository repository;

    private final AllowedDomainObjectTypesRatingPolicy allowedDomainObjectTypesRatingPolicy;
    private final RangeRatingPolicy rangeRatingPolicy;

    private final UpdatingRatingPolicy updatingRatingPolicy;
    private final CreationRatingPolicy creationRatingPolicy;

    @Override
    public Mono<Rating> create(RatingCommand.CrateRatingCommand rawCommand) {
        var command = normalizeCommand(rawCommand);
        return Mono.fromCallable(() ->
                allowedDomainObjectTypesRatingPolicy.apply(command.domainObjectType())
                        .then(rangeRatingPolicy.apply(command.domainObjectType(), command.rating()))
                        .then(repository.fetchByDomainObjectAndUser(command.domainObjectType(), command.domainObjectId(), command.userId())
                                .flatMap(domain -> updatingRatingPolicy.apply(domain, command.rating()))
                                .onErrorResume(RatingException.NotFoundRatingException.class,
                                        t -> creationRatingPolicy.apply(command.domainObjectType(), command.domainObjectId(), command.userId(), command.rating())
                                )
                        )
        ).doOnSubscribe(subscription ->
                logger.debug("crating/updating rating: {}-{}-{} {}", command.domainObjectType(), command.domainObjectId(), command.userId(), command.rating())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("crated/updated rating: {}-{}-{} {}", command.domainObjectType(), command.domainObjectId(), command.userId(), command.rating())
        ).doOnError(e ->
                logger.error("exception occurred while crating/updating rating: {}-{}-{} {}, exception: {}", command.domainObjectType(), command.domainObjectId(), command.userId(), command.rating(), e.toString())
        );
    }

    private RatingCommand.CrateRatingCommand normalizeCommand(RatingCommand.CrateRatingCommand command) {
        return new RatingCommand.CrateRatingCommand(
                command.domainObjectType().toUpperCase(),
                command.domainObjectId(),
                command.userId(),
                command.rating()
        );
    }

    @Override
    public Mono<Rating> remove(RatingCommand.RemoveRatingCommand rawCommand) {
        var command = normalizeCommand(rawCommand);
        return Mono.fromCallable(() ->
                repository.fetchByDomainObjectAndUser(command.domainObjectType(), command.domainObjectId(), command.userId())
                        .flatMap(domain -> mongoTemplate.remove(domain)
                                    .then(eventPublisher.publish(new RatingEvent.RatingRemovedEvent(domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId(), domain.getUserId(), domain.getRating())))
                                    .thenReturn(domain)
                        )
        ).doOnSubscribe(subscription ->
                logger.debug("removing rating: {}-{}-{}", command.domainObjectType(), command.domainObjectId(), command.userId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("removed rating: {}-{}-{}", command.domainObjectType(), command.domainObjectId(), command.userId())
        ).doOnError(e ->
                logger.error("exception occurred while removing rating: {}-{}-{}, exception: {}", command.domainObjectType(), command.domainObjectId(), command.userId(), e.toString())
        );
    }
    private RatingCommand.RemoveRatingCommand normalizeCommand(RatingCommand.RemoveRatingCommand command) {
        return new RatingCommand.RemoveRatingCommand(
                command.domainObjectType().toUpperCase(),
                command.domainObjectId(),
                command.userId()
        );
    }
}
