package pl.app.feedback.rating.application.domain.model;

import org.bson.types.ObjectId;

public interface RatingEvent {
    record RatingCreatedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId,
            Double rating
    ) {
    }

    record RatingUpdatedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId,
            Double rating,
            Double oldRating
    ) {
    }

    record RatingRemovedEvent(
            ObjectId id,
            String domainObjectType,
            String domainObjectId,
            String userId,
            Double rating
    ) {
    }
}
