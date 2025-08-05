package pl.app.feedback.rating.application.domain.model;

import pl.app.common.exception.NotFoundException;
import pl.app.common.exception.ValidationException;

import java.text.MessageFormat;

public interface RatingException {
    class NotFoundRatingException extends NotFoundException {
        public NotFoundRatingException() {
            super("not found rating");
        }

        public NotFoundRatingException(String message) {
            super(message);
        }
    }

    class InvalidDomainObjectTypeException extends ValidationException {
        public InvalidDomainObjectTypeException() {
            super("domain object type is invalid");
        }

        public InvalidDomainObjectTypeException(String message) {
            super(message);
        }

        public static InvalidDomainObjectTypeException domainObjectType(String domainObjectType) {
            return new InvalidDomainObjectTypeException(
                    MessageFormat.format("domain object type is invalid: {0}", domainObjectType)
            );
        }
    }

    class InvalidRatingException extends ValidationException {
        public InvalidRatingException() {
            super("rating is invalid");
        }

        public InvalidRatingException(String message) {
            super(message);
        }

        public static InvalidRatingException range(Double n1, Double n2) {
            return new InvalidRatingException(
                    MessageFormat.format("reaction is invalid. Must be in range: ({0}, {1})", n1, n2)
            );
        }
    }
}
