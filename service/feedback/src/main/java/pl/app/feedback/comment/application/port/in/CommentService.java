package pl.app.feedback.comment.application.port.in;

import pl.app.feedback.comment.application.domain.model.Comment;
import reactor.core.publisher.Mono;

public interface CommentService {
    Mono<Comment> create(CommentCommand.CreateCommentCommand command);

    Mono<Comment> update(CommentCommand.UpdateCommentCommand command);

    Mono<Comment> remove(CommentCommand.RemoveCommentCommand command);

    Mono<Comment> hide(CommentCommand.HideCommentCommand command);

    Mono<Comment> restore(CommentCommand.RestoreCommentCommand command);
}