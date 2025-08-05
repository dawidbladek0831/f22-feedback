package pl.app.feedback.comment.application.port.in;

import org.bson.types.ObjectId;

public interface CommentCommand {

    record CreateCommentCommand(
            String domainObjectType,
            String domainObjectId,
            String userId,
            String content,
            ObjectId parentId
    ) {
    }

    record UpdateCommentCommand(
            ObjectId commentId,
            String content
    ) {
    }

    record RemoveCommentCommand(
            ObjectId commentId
    ) {
    }

    record HideCommentCommand(
            ObjectId commentId
    ) {
    }

    record RestoreCommentCommand(
            ObjectId commentId
    ) {
    }
}