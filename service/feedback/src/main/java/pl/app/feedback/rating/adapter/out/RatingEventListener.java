package pl.app.feedback.rating.adapter.out;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.feedback.rating.application.domain.model.RatingEvent;
import pl.app.feedback.reaction.application.domain.model.ReactionEvent;

@Component
@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@RequiredArgsConstructor
class RatingEventListener {
    private static final Logger logger = LoggerFactory.getLogger(RatingEventListener.class);
    private final RatingEventProcessor ratingEventProcessor;

    @KafkaListener(
            id = "rating-created-event-listener--rating",
            groupId = "${app.kafka.consumer.group-id}--rating",
            topics = "${app.kafka.topic.rating-created.name}"
    )
    public void RatingCreatedEvent(ConsumerRecord<ObjectId, RatingEvent.RatingCreatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        ratingEventProcessor.submit(record.value());
    }
    @KafkaListener(
            id = "rating-updated-event-listener--rating",
            groupId = "${app.kafka.consumer.group-id}--rating",
            topics = "${app.kafka.topic.rating-updated.name}"
    )
    public void RatingUpdatedEvent(ConsumerRecord<ObjectId, RatingEvent.RatingUpdatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        ratingEventProcessor.submit(record.value());
    }

    @KafkaListener(
            id = "rating-removed-event-listener--rating",
            groupId = "${app.kafka.consumer.group-id}--rating",
            topics = "${app.kafka.topic.rating-removed.name}"
    )
    public void RatingRemovedEvent(ConsumerRecord<ObjectId, RatingEvent.RatingRemovedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        ratingEventProcessor.submit(record.value());
    }
}
