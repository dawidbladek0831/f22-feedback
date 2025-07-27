package pl.app.common.event;

import reactor.core.publisher.Mono;

import java.util.Collection;

public interface EventPublisher {
    Mono<Void> publish(Object event);

    Mono<Void> publish(Collection<Object> events);
}
