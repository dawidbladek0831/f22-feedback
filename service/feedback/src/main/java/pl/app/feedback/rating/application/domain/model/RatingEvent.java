package pl.app.feedback.rating.application.domain.model;

import org.bson.types.ObjectId;
import pl.app.common.event.DomainObjectEvent;

public interface RatingEvent {
    record RatingCreatedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId,
            Double rating
    ) implements DomainObjectEvent {
    }

    record RatingUpdatedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId,
            Double rating,
            Double oldRating
    ) implements DomainObjectEvent {
    }

    record RatingRemovedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId,
            Double rating
    ) implements DomainObjectEvent {
    }
}
