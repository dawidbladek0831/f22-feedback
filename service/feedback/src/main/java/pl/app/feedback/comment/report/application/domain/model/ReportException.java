package pl.app.feedback.comment.report.application.domain.model;

import pl.app.common.exception.InvalidStateException;
import pl.app.common.exception.NotFoundException;

public interface ReportException {
    class NotFoundReportException extends NotFoundException {
        public NotFoundReportException() {
            super("not found report");
        }

        public NotFoundReportException(String message) {
            super(message);
        }
    }

    class InvalidStateReportException extends InvalidStateException {
        public InvalidStateReportException() {
            super();
        }

        public InvalidStateReportException(String message) {
            super(message);
        }
    }
}
