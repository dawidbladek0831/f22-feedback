package pl.app.feedback.reaction.adapter.out;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.app.feedback.reaction.application.domain.model.ReactionEvent;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@Component
@RequiredArgsConstructor
class ReactionEventProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ReactionEventProcessor.class);
    private final ReadModelSynchronizer handler;
    private final Sinks.Many<Object> sink = Sinks.many().multicast().onBackpressureBuffer();

    @PostConstruct
    public void init() {
        sink.asFlux()
                .groupBy(event -> {
                    if (event instanceof ReactionEvent.ReactionAddedEvent e) return e.domainObjectId();
                    if (event instanceof ReactionEvent.ReactionRemovedEvent e) return e.domainObjectId();
                    return "unknown";
                })
                .flatMap(groupedFlux ->
                        groupedFlux.concatMap(event ->
                                Mono.delay(Duration.ofSeconds(1))
                                        .then(Mono.defer(() -> {
                                            if (event instanceof ReactionEvent.ReactionAddedEvent e)
                                                return handler.handle(e);
                                            if (event instanceof ReactionEvent.ReactionRemovedEvent e)
                                                return handler.handle(e);
                                            logger.warn("unknown event: {}", event);
                                            return Mono.empty();
                                        }))
                        )
                )
                .subscribe();
    }

    public void submit(Object event) {
        sink.tryEmitNext(event);
    }
}
