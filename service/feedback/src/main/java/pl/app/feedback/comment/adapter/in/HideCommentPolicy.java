package pl.app.feedback.comment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.feedback.comment.application.domain.model.CommentException;
import pl.app.feedback.comment.application.port.in.CommentCommand;
import pl.app.feedback.comment.application.port.in.CommentService;
import pl.app.feedback.comment.report.application.domain.model.ReportEvent;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@RequiredArgsConstructor
class HideCommentPolicy {
    private static final Logger logger = LoggerFactory.getLogger(HideCommentPolicy.class);
    private final CommentService service;

    @KafkaListener(
            id = "report-approved-event-listener--comment",
            groupId = "${app.kafka.consumer.group-id}--comment",
            topics = "${app.kafka.topic.report-approved.name}"
    )
    public void hide(ConsumerRecord<ObjectId, ReportEvent.ReportApprovedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        var event = record.value();
        service.hide(new CommentCommand.HideCommentCommand(event.commentId()))
                .onErrorResume(CommentException.InvalidStateReportException.class, ex -> Mono.empty())
                .block();
    }

}
