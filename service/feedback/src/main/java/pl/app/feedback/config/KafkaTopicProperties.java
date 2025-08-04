package pl.app.feedback.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import pl.app.feedback.comment.application.domain.model.CommentEvent;
import pl.app.feedback.comment.report.application.domain.model.ReportEvent;
import pl.app.feedback.rating.application.domain.model.RatingEvent;
import pl.app.feedback.reaction.application.domain.model.ReactionEvent;

import java.text.MessageFormat;
import java.util.List;

@Component
@Data
@ConfigurationProperties(prefix = "app.kafka.topic")
@PropertySource("classpath:kafka.properties")
class KafkaTopicProperties {
    private static final Logger logger = LoggerFactory.getLogger(KafkaTopicProperties.class);

    private Topic reactionCreated;
    private Topic reactionAdded;
    private Topic reactionRemoved;

    private Topic ratingCreated;
    private Topic ratingUpdated;
    private Topic ratingRemoved;

    private Topic reportCreated;
    private Topic reportApproved;
    private Topic reportRejected;

    private Topic commentCreated;
    private Topic commentUpdated;
    private Topic commentRemoved;
    private Topic commentHidden;
    private Topic commentRestored;

    public List<Topic> getAllTopics() {
        return List.of(
                reactionCreated,
                reactionAdded,
                reactionRemoved,

                ratingCreated,
                ratingUpdated,
                ratingRemoved,

                reportCreated,
                reportApproved,
                reportRejected,

                commentCreated,
                commentUpdated,
                commentRemoved,
                commentHidden,
                commentRestored
        );
    }

    public List<String> getAllTopicNames() {
        return getAllTopics().stream()
                .map(Topic::getTopicNames)
                .flatMap(List::stream)
                .toList();
    }

    public Topic getTopic(Object event) {
        return switch (event) {
            case ReactionEvent.ReactionCreatedEvent e -> reactionCreated;
            case ReactionEvent.ReactionAddedEvent e -> reactionAdded;
            case ReactionEvent.ReactionRemovedEvent e -> reactionRemoved;

            case RatingEvent.RatingCreatedEvent e -> ratingCreated;
            case RatingEvent.RatingUpdatedEvent e -> ratingUpdated;
            case RatingEvent.RatingRemovedEvent e -> ratingRemoved;

            case ReportEvent.ReportCreatedEvent e -> reportCreated;
            case ReportEvent.ReportApprovedEvent e -> reportApproved;
            case ReportEvent.ReportRejectedEvent e -> reportRejected;

            case CommentEvent.CommentCreatedEvent e -> commentCreated;
            case CommentEvent.CommentUpdatedEvent e -> commentUpdated;
            case CommentEvent.CommentRemovedEvent e -> commentRemoved;
            case CommentEvent.CommentHiddenEvent e -> commentHidden;
            case CommentEvent.CommentRestoredEvent e -> commentRestored;

            default -> {
                logger.error("event {} is not configured in properties", event.getClass().getSimpleName());
                throw new RuntimeException(
                        MessageFormat.format("event {0} is not configured in properties", event.getClass().getSimpleName())
                );
            }
        };
    }

    @Setter
    @Getter
    public static class Topic {
        private String name;
        private Integer partitions;
        private String dtlSuffix;

        public Topic() {
            this.name = "NAME_NOT_CONFIGURED";
            this.partitions = 1;
            this.dtlSuffix = ".DTL";
        }

        public List<String> getTopicNames() {
            return List.of(name, getDtlTopicName());
        }

        public String getDtlTopicName() {
            return name + dtlSuffix;
        }
    }
}
