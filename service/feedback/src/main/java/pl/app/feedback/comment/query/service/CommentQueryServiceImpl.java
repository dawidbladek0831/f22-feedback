package pl.app.feedback.comment.query.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.feedback.comment.application.domain.model.Comment;
import pl.app.feedback.comment.application.domain.model.CommentException;
import pl.app.feedback.comment.query.port.CommentQueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RequiredArgsConstructor
class CommentQueryServiceImpl implements CommentQueryService {
    private final ReactiveMongoTemplate mongoTemplate;


    @Override
    public Flux<Comment> fetchAll() {
        return mongoTemplate.query(Comment.class).all();
    }

    @Override
    public Mono<Comment> fetchBy(ObjectId commentId) {
        return mongoTemplate.query(Comment.class)
                .matching(Query.query(Criteria
                        .where("_id").is(commentId)
                )).one()
                .switchIfEmpty(Mono.error(new CommentException.NotFoundCommentException()));
    }

    @Override
    public Flux<Comment> fetchAllBy(String domainObjectType, String domainObjectId, String userId, ObjectId parentId, Comment.Status status, String cursor, Integer pageSize) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(domainObjectType)) {
            criteria = criteria.and("domainObjectType").is(domainObjectType);
        }
        if (Objects.nonNull(domainObjectId)) {
            criteria = criteria.and("domainObjectId").is(domainObjectId);
        }
        if (Objects.nonNull(userId)) {
            criteria = criteria.and("userId").is(userId);
        }
        if (Objects.nonNull(parentId)) {
            criteria = criteria.and("parentId").is(parentId);
        }
        if (Objects.nonNull(status)) {
            criteria = criteria.and("status").is(status);
        }

        if (Objects.nonNull(cursor)) {
            if (ObjectId.isValid(cursor)) {
                criteria = criteria.and("_id").gt(new ObjectId(cursor));
            }
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 50;
        }
        Query query = Query.query(criteria).limit(pageSize)
                .with(Sort.by(Sort.Direction.ASC, "_id"));
        return mongoTemplate.query(Comment.class)
                .matching(query)
                .all();
    }

}
