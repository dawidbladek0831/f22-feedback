package pl.app.feedback.reaction.query.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.feedback.reaction.application.domain.model.Reaction;
import pl.app.feedback.reaction.application.domain.model.ReactionException;
import pl.app.feedback.reaction.query.dto.DomainObjectReactionDto;
import pl.app.feedback.reaction.query.dto.ReactionDto;
import pl.app.feedback.reaction.query.model.DomainObjectReaction;
import pl.app.feedback.reaction.query.model.UserReaction;
import pl.app.feedback.reaction.query.port.DomainObjectReactionQueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RequiredArgsConstructor
class DomainObjectReactionQueryServiceImpl implements DomainObjectReactionQueryService {
    private final DomainObjectReactionRepository domainObjectReactionRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final ReactionMapper mapper;

    @Override
    public Mono<DomainObjectReactionDto> fetchDomainObjectReaction(String domainObjectType, String domainObjectId) {
        return domainObjectReactionRepository.findOneByDomainObjectTypeAndDomainObjectId(domainObjectType, domainObjectId)
                .defaultIfEmpty(new DomainObjectReaction(domainObjectType, domainObjectId))
                .map(e -> mapper.map(e, DomainObjectReactionDto.class));
    }

    @Override
    public Flux<ReactionDto> fetchAllUserReaction(String userId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("userId").is(userId)), UserReaction.class)
                .defaultIfEmpty(new UserReaction(userId))
                .map(UserReaction::getReactions)
                .flatMapMany(Flux::fromIterable)
                .map(e -> mapper.map(e, ReactionDto.class));
    }

    @Override
    public Mono<ReactionDto> fetchBy(String userId, String domainObjectType, String domainObjectId) {
        return mongoTemplate.query(Reaction.class)
                .matching(Query.query(Criteria
                        .where("domainObjectType").is(domainObjectType)
                        .and("domainObjectId").is(domainObjectId)
                        .and("userId").is(userId)
                )).one()
                .map(e -> mapper.map(e, ReactionDto.class))
                .switchIfEmpty(Mono.error(ReactionException.NotFoundReactionException::new));
    }

    @Override
    public Flux<ReactionDto> fetchAllBy(String userId, String domainObjectType, String domainObjectId, String cursor, Integer pageSize) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(userId)) {
            criteria = criteria.and("userId").is(userId);
        }
        if (Objects.nonNull(domainObjectType)) {
            criteria = criteria.and("domainObjectType").is(domainObjectType);
        }
        if (Objects.nonNull(domainObjectId)) {
            criteria = criteria.and("domainObjectId").is(domainObjectId);
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
        return mongoTemplate.query(Reaction.class)
                .matching(query)
                .all()
                .map(e -> mapper.map(e, ReactionDto.class));
    }
}
