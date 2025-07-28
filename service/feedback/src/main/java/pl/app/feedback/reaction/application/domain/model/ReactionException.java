package pl.app.feedback.reaction.application.domain.model;

import pl.app.common.exception.NotFoundException;
import pl.app.common.exception.ValidationException;

import java.text.MessageFormat;

public interface ReactionException {
    class DuplicatedReactionException extends ValidationException {
        public DuplicatedReactionException() {
            super("there is already such a reaction for this object");
        }

        public DuplicatedReactionException(String message) {
            super(message);
        }

        public static DuplicatedReactionException reaction(String reaction) {
            return new DuplicatedReactionException(
                    MessageFormat.format("there is already such a reaction for this object: {0}", reaction)
            );
        }
    }

    class NotFoundReactionException extends NotFoundException {
        public NotFoundReactionException() {
            super("not found user reaction");
        }

        public NotFoundReactionException(String message) {
            super(message);
        }

        public static NotFoundReactionException reaction(String reaction) {
            return new NotFoundReactionException(
                    MessageFormat.format("not found user reaction: {0}", reaction)
            );
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

    class InvalidReactionException extends ValidationException {
        public InvalidReactionException() {
            super("reaction is invalid");
        }

        public InvalidReactionException(String message) {
            super(message);
        }

        public static InvalidReactionException reaction(String reaction) {
            return new InvalidReactionException(
                    MessageFormat.format("reaction is invalid: {0}", reaction)
            );
        }
    }
}
