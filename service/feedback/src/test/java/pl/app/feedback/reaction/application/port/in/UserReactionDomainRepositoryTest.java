package pl.app.feedback.reaction.application.port.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import pl.app.feedback.reaction.application.domain.UserReaction;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserReactionDomainRepositoryTest {
    @Autowired
    UserReactionDomainRepository repository;

    @Autowired
    ReactiveMongoTemplate mongoTemplate;

    @Test
    void whenUserSendFirstReaction_thenReactionIsCratedAndUpdated() {
        var domainObjectType = "POST";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();

        var domain = new UserReaction(domainObjectType, domainObjectId, userId);
        mongoTemplate.save(domain).block();

        StepVerifier.create(
                repository.fetchByDomainObjectAndUser(domainObjectType, domainObjectId, userId)
        ).assertNext(d -> {
            assertThat(domain).isNotNull();
            assertThat(domain.getDomainObjectType()).isEqualTo(domainObjectType);
        }).verifyComplete();
    }
}