package pl.app.feedback.reaction.application.domain.model;

import org.bson.types.ObjectId;

public interface ReactionEvent {
    record ReactionCreatedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId
    ) {
    }

    record ReactionAddedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId,
            String reaction
    ) {
    }

    record ReactionRemovedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId,
            String reaction
    ) {
    }
}
