package pl.app.feedback.reaction.application.port.in;

public interface ReactionCommand {

    record AddReactionCommand(
            String domainObjectType,
            String domainObjectId,
            String userId,
            String reaction
    ) {
    }

    record RemoveReactionCommand(
            String domainObjectType,
            String domainObjectId,
            String userId,
            String reaction
    ) {
    }
}