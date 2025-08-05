package pl.app.feedback.reaction.query.service;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pl.app.feedback.reaction.query.model.DomainObjectReaction;
import reactor.core.publisher.Mono;

@Repository
interface DomainObjectReactionRepository extends ReactiveMongoRepository<DomainObjectReaction, ObjectId> {
    Mono<DomainObjectReaction> findOneByDomainObjectTypeAndDomainObjectId(String domainObjectType, String domainObjectId);
}
