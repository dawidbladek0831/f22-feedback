package pl.app.feedback.rating.application.port.in;

public interface RatingCommand {

    record UpsertRatingCommand(
            String domainObjectType,
            String domainObjectId,
            String userId,
            Double rating
    ) {
    }

    record RemoveRatingCommand(
            String domainObjectType,
            String domainObjectId,
            String userId
    ) {
    }
}