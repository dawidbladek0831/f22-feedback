package pl.app.feedback.comment.query.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.feedback.comment.query.model.DomainObjectComment;
import pl.app.feedback.comment.query.port.DomainObjectCommentQueryService;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class DomainObjectCommentQueryServiceImpl implements DomainObjectCommentQueryService {
    private final ReactiveMongoTemplate mongoTemplate;


    @Override
    public Mono<DomainObjectComment> fetchAllBy(String domainObjectType, String domainObjectId) {
        return mongoTemplate.query(DomainObjectComment.class)
                .matching(Query.query(Criteria
                        .where("domainObjectType").is(domainObjectType)
                        .and("domainObjectId").is(domainObjectId)
                )).one()
                .defaultIfEmpty(new DomainObjectComment(domainObjectType, domainObjectId));
    }
}
