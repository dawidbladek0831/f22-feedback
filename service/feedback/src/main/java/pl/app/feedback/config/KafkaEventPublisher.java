package pl.app.feedback.config;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pl.app.common.event.Event;
import pl.app.common.event.EventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Primary
@Component
@RequiredArgsConstructor
class KafkaEventPublisher implements EventPublisher {
    private final Logger logger = LoggerFactory.getLogger(KafkaEventPublisher.class);
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicProperties topicProperties;

    @Override
    public Mono<Void> publish(Object event) {
        if (event instanceof Collection<?> collection) {
            return publishCollection(collection);
        }
        if (event instanceof Event e) {
            return Mono.fromFuture(kafkaTemplate.send(topicProperties.getTopic(event).getName(), e.id(), event)).then();
        }
        logger.error("event {} is not configured in EventPublisher", event.getClass().getSimpleName());
        return Mono.empty();
    }

    @Override
    public Mono<Void> publishCollection(Collection<?> events) {
        return Flux.fromIterable(events)
                .flatMap(this::publish)
                .then();
    }
}
