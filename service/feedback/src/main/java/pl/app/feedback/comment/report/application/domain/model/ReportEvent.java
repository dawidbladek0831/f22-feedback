package pl.app.feedback.comment.report.application.domain.model;

import org.bson.types.ObjectId;
import pl.app.common.event.Event;

public interface ReportEvent {
    record ReportCreatedEvent(
            ObjectId id,
            ObjectId commentId,
            String reason,
            String userId
    ) implements Event {
    }

    record ReportApprovedEvent(
            ObjectId id,
            ObjectId commentId
    ) implements Event {
    }

    record ReportRejectedEvent(
            ObjectId id,
            ObjectId commentId
    ) implements Event {
    }
}
