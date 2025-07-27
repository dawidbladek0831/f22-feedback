package pl.app.feedback.reaction.application.port.in;

public interface ReactionCommand {

    record AddUserReactionCommand(
            String domainObjectType,
            String domainObjectId,
            String userId,
            String reaction
    ) {
    }

    record RemoveUserReactionCommand(
            String domainObjectType,
            String domainObjectId,
            String userId,
            String reaction
    ) {
    }
}