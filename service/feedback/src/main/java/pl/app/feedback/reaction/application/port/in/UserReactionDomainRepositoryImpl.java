package pl.app.feedback.reaction.application.port.in;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.feedback.reaction.application.domain.ReactionException;
import pl.app.feedback.reaction.application.domain.UserReaction;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class UserReactionDomainRepositoryImpl implements UserReactionDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<UserReaction> fetchByDomainObjectAndUser(String domainObjectType, String domainObjectId, String userId) {
        return mongoTemplate.query(UserReaction.class)
                .matching(Query.query(Criteria.where("domainObjectType").is(domainObjectType)
                        .and("domainObjectId").is(domainObjectId)
                        .and("userId").is(userId)
                )).one()
                .switchIfEmpty(Mono.error(ReactionException.NotFoundUserReactionException::new));
    }


}
