package pl.app.feedback.shared;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import pl.app.common.event.EventPublisher;
import pl.app.feedback.reaction.application.domain.model.ReactionEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Component
@Primary
@RequiredArgsConstructor
class CloudStreamEventPublisher implements EventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(CloudStreamEventPublisher.class);
    private static final String DEFAULT_SUFFIX = "-out-0";
    private final StreamBridge streamBridge;

    @Override
    public Mono<Void> publish(Object event) {
        if (event instanceof Collection<?> collection) {
            return publishCollection(collection);
        }
        return switch (event) {
            case ReactionEvent.ReactionCreatedEvent e -> defaultEventPublish(e);
            case ReactionEvent.ReactionAddedEvent e -> defaultEventPublish(e);
            case ReactionEvent.ReactionRemovedEvent e -> defaultEventPublish(e);
            default -> {
                logger.error("event {} is not configured in EventPublisher", event.getClass().getSimpleName());
                yield Mono.empty();
            }
        };
    }

    private Mono<Void> defaultEventPublish(Object event) {
        var topic = event.getClass().getSimpleName().substring(0, 1).toLowerCase() + event.getClass().getSimpleName().substring(1) + DEFAULT_SUFFIX;
        return Mono.fromRunnable(() -> streamBridge.send(topic , event));
    }

    @Override
    public Mono<Void> publishCollection(Collection<?> events) {
        return Flux.fromIterable(events)
                .flatMap(this::publish)
                .then();
    }
}
