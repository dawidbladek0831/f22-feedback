package pl.app.feedback.comment.query.port;

import pl.app.feedback.comment.query.model.DomainObjectComment;
import reactor.core.publisher.Mono;

public interface DomainObjectCommentQueryService {
    Mono<DomainObjectComment> fetchAllBy(String domainObjectType, String domainObjectId);
}
