package pl.app.feedback.reaction.query.adapter.out;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.feedback.reaction.query.model.DomainObjectReaction;
import pl.app.feedback.reaction.query.model.UserReaction;
import pl.app.feedback.reaction.query.port.DomainObjectReactionQueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class DomainObjectReactionQueryServiceImpl implements DomainObjectReactionQueryService {
    private final DomainObjectReactionRepository domainObjectReactionRepository;
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<DomainObjectReaction> fetchAll() {
        return domainObjectReactionRepository.findAll();
    }

    @Override
    public Mono<DomainObjectReaction> fetchBy(String domainObjectType, String domainObjectId) {
        return domainObjectReactionRepository.findOneByDomainObjectTypeAndDomainObjectId(domainObjectType, domainObjectId)
                .defaultIfEmpty(new DomainObjectReaction(domainObjectType, domainObjectId));
    }

    @Override
    public Mono<UserReaction> fetchBy(String userId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("userId").is(userId)), UserReaction.class)
                .defaultIfEmpty(new UserReaction(userId));
    }
}
