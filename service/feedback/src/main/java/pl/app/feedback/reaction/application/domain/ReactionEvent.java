package pl.app.feedback.reaction.application.domain;

import org.bson.types.ObjectId;

public interface ReactionEvent {
    record UserReactionCreatedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId
    ) {
    }

    record UserReactionAddedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId,
            String reaction
    ) {
    }

    record UserReactionRemovedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId,
            String reaction
    ) {
    }
}
