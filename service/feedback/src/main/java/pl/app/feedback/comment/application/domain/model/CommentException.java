package pl.app.feedback.comment.application.domain.model;

import pl.app.common.exception.InvalidStateException;
import pl.app.common.exception.NotFoundException;

public interface CommentException {
    class NotFoundCommentException extends NotFoundException {
        public NotFoundCommentException() {
            super("not found comment");
        }

        public NotFoundCommentException(String message) {
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
