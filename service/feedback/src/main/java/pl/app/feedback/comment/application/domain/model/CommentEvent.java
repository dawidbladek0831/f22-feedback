package pl.app.feedback.comment.application.domain.model;

import org.bson.types.ObjectId;
import pl.app.common.event.DomainObjectEvent;

public interface CommentEvent {
    record CommentCreatedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId,
            String content,
            ObjectId parentId
    ) implements DomainObjectEvent {
    }

    record CommentUpdatedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String content
    ) implements DomainObjectEvent {
    }

    record CommentRemovedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId
    ) implements DomainObjectEvent {
    }

    record CommentHiddenEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId
    ) implements DomainObjectEvent {
    }

    record CommentRestoredEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId
    ) implements DomainObjectEvent {
    }
}
