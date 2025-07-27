package pl.app.feedback.reaction.application.domain;

import pl.app.common.exception.*;

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

    class NotFoundUserReactionException extends NotFoundException {
        public NotFoundUserReactionException() {
            super("not found user reaction");
        }

        public NotFoundUserReactionException(String message) {
            super(message);
        }

        public static NotFoundUserReactionException reaction(String reaction) {
            return new NotFoundUserReactionException(
                    MessageFormat.format("not found user reaction: {0}", reaction)
            );
        }
    }
}
