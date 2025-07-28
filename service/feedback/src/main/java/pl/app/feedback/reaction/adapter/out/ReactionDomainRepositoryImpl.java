package pl.app.feedback.reaction.adapter.out;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.feedback.reaction.application.domain.model.Reaction;
import pl.app.feedback.reaction.application.domain.model.ReactionException;
import pl.app.feedback.reaction.application.port.out.ReactionDomainRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class ReactionDomainRepositoryImpl implements ReactionDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Reaction> fetchByDomainObjectAndUser(String domainObjectType, String domainObjectId, String userId) {
        return mongoTemplate.query(Reaction.class)
                .matching(Query.query(Criteria
                        .where("domainObjectType").is(domainObjectType)
                        .and("domainObjectId").is(domainObjectId)
                        .and("userId").is(userId)
                )).one()
                .switchIfEmpty(Mono.error(ReactionException.NotFoundReactionException::new));
    }


}
