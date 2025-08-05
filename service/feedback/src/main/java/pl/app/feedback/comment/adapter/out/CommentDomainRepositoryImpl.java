package pl.app.feedback.comment.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.feedback.comment.application.domain.model.Comment;
import pl.app.feedback.comment.application.domain.model.CommentException;
import pl.app.feedback.comment.application.port.out.CommentDomainRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class CommentDomainRepositoryImpl implements CommentDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Comment> fetch(ObjectId reportId) {
        return mongoTemplate.query(Comment.class)
                .matching(Query.query(Criteria
                        .where("_id").is(reportId)
                )).one()
                .switchIfEmpty(Mono.error(CommentException.NotFoundCommentException::new));
    }
}
