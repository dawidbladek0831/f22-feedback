package pl.app.feedback.shared;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pl.app.common.event.EventPublisher;
import pl.app.feedback.config.KafkaTopicProperties;
import pl.app.feedback.reaction.application.domain.model.ReactionEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Primary
@Component
@RequiredArgsConstructor
class KafkaEventPublisher implements EventPublisher {
    private final Logger logger = LoggerFactory.getLogger(KafkaEventPublisher.class);
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicProperties topicNames;

    @Override
    public Mono<Void> publish(Object event) {
        if (event instanceof Collection<?> collection) {
            return publishCollection(collection);
        }
        return switch (event) {
            case ReactionEvent.ReactionCreatedEvent e -> Mono.fromFuture(kafkaTemplate.send(topicNames.getReactionCreated().getName(), e.id(), event)).then();
            case ReactionEvent.ReactionAddedEvent e -> Mono.fromFuture(kafkaTemplate.send(topicNames.getReactionAdded().getName(), e.id(), event)).then();
            case ReactionEvent.ReactionRemovedEvent e -> Mono.fromFuture(kafkaTemplate.send(topicNames.getReactionRemoved().getName(), e.id(), event)).then();

            default -> {
                logger.error("event {} is not configured in EventPublisher", event.getClass().getSimpleName());
                yield Mono.empty();
            }
        };
    }

    @Override
    public Mono<Void> publishCollection(Collection<?> events) {
        return Flux.fromIterable(events)
                .flatMap(this::publish)
                .then();
    }
}
