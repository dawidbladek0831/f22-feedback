package pl.app.feedback.comment.report.application.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comment_report")
@Getter
@NoArgsConstructor
public class Report {
    @Id
    private ObjectId id;
    private ObjectId commentId;
    private String reason;
    private String userId;
    private Status status;

    public Report(ObjectId commentId, String reason, String userId) {
        this.id = ObjectId.get();
        this.commentId = commentId;
        this.reason = reason;
        this.userId = userId;
        this.status = Status.PENDING;
    }

    public void approve() {
        if (status != Status.PENDING) {
            throw new ReportException.InvalidStateReportException();
        }
        this.status = Status.APPROVED;
    }

    public void reject() {
        if (status != Status.PENDING) {
            throw new ReportException.InvalidStateReportException();
        }
        this.status = Status.REJECTED;
    }

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }
}
