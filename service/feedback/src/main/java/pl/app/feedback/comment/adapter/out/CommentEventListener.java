package pl.app.feedback.comment.adapter.out;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.feedback.comment.application.domain.model.CommentEvent;

@Component
@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@RequiredArgsConstructor
class CommentEventListener {
    private static final Logger logger = LoggerFactory.getLogger(CommentEventListener.class);
    private final DomainObjectCommentReadModelSynchronizer synchronizer;

    @KafkaListener(
            id = "comment-created-event-listener--comment--domain-object-comment-synchronizer",
            groupId = "${app.kafka.consumer.group-id}--comment--domain-object-comment-synchronizer",
            topics = "${app.kafka.topic.comment-created.name}"
    )
    public void CommentCreatedEvent(ConsumerRecord<ObjectId, CommentEvent.CommentCreatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        synchronizer.handle(record.value()).block();
    }

    @KafkaListener(
            id = "comment-updated-event-listener--comment--domain-object-comment-synchronizer",
            groupId = "${app.kafka.consumer.group-id}--comment--domain-object-comment-synchronizer",
            topics = "${app.kafka.topic.comment-updated.name}"
    )
    public void CommentUpdatedEvent(ConsumerRecord<ObjectId, CommentEvent.CommentUpdatedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        synchronizer.handle(record.value()).block();
    }

    @KafkaListener(
            id = "comment-removed-event-listener--comment--domain-object-comment-synchronizer",
            groupId = "${app.kafka.consumer.group-id}--comment--domain-object-comment-synchronizer",
            topics = "${app.kafka.topic.comment-removed.name}"
    )
    public void CommentRemovedEvent(ConsumerRecord<ObjectId, CommentEvent.CommentRemovedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        synchronizer.handle(record.value()).block();
    }

    @KafkaListener(
            id = "comment-hidden-event-listener--comment--domain-object-comment-synchronizer",
            groupId = "${app.kafka.consumer.group-id}--comment--domain-object-comment-synchronizer",
            topics = "${app.kafka.topic.comment-hidden.name}"
    )
    public void CommentHiddenEvent(ConsumerRecord<ObjectId, CommentEvent.CommentHiddenEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        synchronizer.handle(record.value()).block();
    }

    @KafkaListener(
            id = "comment-restored-event-listener--comment--domain-object-comment-synchronizer",
            groupId = "${app.kafka.consumer.group-id}--comment--domain-object-comment-synchronizer",
            topics = "${app.kafka.topic.comment-restored.name}"
    )
    public void CommentRestoredEvent(ConsumerRecord<ObjectId, CommentEvent.CommentRestoredEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        synchronizer.handle(record.value()).block();
    }
}
