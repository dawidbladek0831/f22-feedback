package pl.app.feedback.reaction.application.domain.model;

import org.bson.types.ObjectId;
import pl.app.common.event.DomainObjectEvent;

public interface ReactionEvent {
    record ReactionCreatedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId
    ) implements DomainObjectEvent {
    }

    record ReactionAddedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId,
            String reaction
    ) implements DomainObjectEvent {
    }

    record ReactionRemovedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId,
            String reaction
    ) implements DomainObjectEvent {
    }
}
