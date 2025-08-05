package pl.app.feedback.comment.query.port;

import org.bson.types.ObjectId;
import pl.app.feedback.comment.application.domain.model.Comment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentQueryService {
    Flux<Comment> fetchAll();

    Mono<Comment> fetchBy(ObjectId commentId);

    Flux<Comment> fetchAllBy(String domainObjectType, String domainObjectId, String userId, ObjectId parentId, Comment.Status status, String cursor, Integer pageSize);
}
