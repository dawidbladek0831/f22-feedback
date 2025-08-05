package pl.app.feedback.comment.report.application.port.in;

import org.bson.types.ObjectId;

public interface ReportCommand {

    record CreateReportCommand(
            ObjectId commentId,
            String reason,
            String userId
    ) {
    }

    record ApproveReportCommand(
            ObjectId reportId
    ) {
    }

    record RejectReportCommand(
            ObjectId reportId
    ) {
    }
}