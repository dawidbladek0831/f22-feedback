package pl.app.feedback.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.app.common.event.EventPublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Component
public class LoggerEventPublisher implements EventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(LoggerEventPublisher.class);

    @Override
    public Mono<Void> publish(Object event) {
        if (event instanceof Collection<?> collection) {
            return publishCollection(collection);
        }
        logger.info("publishing event: {}", event);
        return Mono.empty();
    }

    @Override
    public Mono<Void> publishCollection(Collection<?> events) {
        return Flux.fromIterable(events)
                .flatMap(this::publish)
                .then();
    }
}
