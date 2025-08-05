package pl.app.feedback.comment.application.port.out;

import org.bson.types.ObjectId;
import pl.app.feedback.comment.application.domain.model.Comment;
import reactor.core.publisher.Mono;

public interface CommentDomainRepository {
    Mono<Comment> fetch(ObjectId commentId);
}
