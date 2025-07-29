package pl.app.feedback.rating.adapter.out;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.app.feedback.rating.application.domain.model.RatingEvent;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@Component
@RequiredArgsConstructor
class RatingEventProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RatingEventProcessor.class);
    private final RatingReadModelSynchronizer handler;
    private final Sinks.Many<Object> sink = Sinks.many().multicast().onBackpressureBuffer();

    @PostConstruct
    public void init() {
        sink.asFlux()
                .groupBy(event -> {
                    if (event instanceof RatingEvent.RatingCreatedEvent e) return e.domainObjectId();
                    if (event instanceof RatingEvent.RatingUpdatedEvent e) return e.domainObjectId();
                    if (event instanceof RatingEvent.RatingRemovedEvent e) return e.domainObjectId();
                    return "unknown";
                })
                .flatMap(groupedFlux ->
                        groupedFlux.concatMap(event ->
                                Mono.delay(Duration.ofSeconds(1))
                                        .then(Mono.defer(() -> {
                                            if (event instanceof RatingEvent.RatingCreatedEvent e)
                                                return handler.handle(e);
                                            if (event instanceof RatingEvent.RatingUpdatedEvent e)
                                                return handler.handle(e);
                                            if (event instanceof RatingEvent.RatingRemovedEvent e)
                                                return handler.handle(e);
                                            logger.warn("unknown event: {}", event);
                                            return Mono.empty();
                                        }))
                                        .onErrorContinue((ex, obj) -> logger.error("Failed to process event: {}", obj, ex))
                        )
                )

                .subscribe();
    }

    public void submit(Object event) {
        sink.tryEmitNext(event);
    }
}
