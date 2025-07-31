package pl.app.feedback.reaction.adapter.out;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.feedback.reaction.application.domain.model.ReactionEvent;

@Component
@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@RequiredArgsConstructor
class ReactionEventListener {
    private static final Logger logger = LoggerFactory.getLogger(ReactionEventListener.class);
    private final ReactionReadModelSynchronizer synchronizer;


    @KafkaListener(
            id = "reaction-added-event-listener--reaction",
            groupId = "${app.kafka.consumer.group-id}--reaction",
            topics = "${app.kafka.topic.reaction-added.name}"
    )
    public void reactionAddedEvent(ConsumerRecord<ObjectId, ReactionEvent.ReactionAddedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        synchronizer.handle(event).block();
    }

    @KafkaListener(
            id = "reaction-removed-event-listener--reaction",
            groupId = "${app.kafka.consumer.group-id}--reaction",
            topics = "${app.kafka.topic.reaction-removed.name}"
    )
    public void reactionRemovedEvent(ConsumerRecord<ObjectId, ReactionEvent.ReactionRemovedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        synchronizer.handle(event).block();
    }

}
