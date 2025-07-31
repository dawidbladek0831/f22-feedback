package pl.app.feedback.reaction.adapter.out;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.app.feedback.reaction.application.domain.model.ReactionEvent;
import pl.app.feedback.reaction.query.port.DomainObjectReactionQueryService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class ReactionReadModelSynchronizerTest {
    @Autowired
    private ReactionReadModelSynchronizer service;

    @Autowired
    private DomainObjectReactionQueryService queryService;

    @Test
    void testDomainObjectReactionReadModelAfterEvents() {
        var domainId = ObjectId.get();
        var domainObjectType = "POST";
        var domainObjectId = ObjectId.get().toString();

        var userId = ObjectId.get().toString();
        var userId2 = ObjectId.get().toString();
        var userId3 = ObjectId.get().toString();

        StepVerifier.create(service.handle(new ReactionEvent.ReactionAddedEvent(domainId, domainObjectType, domainObjectId, userId, "LIKE")))
                .expectNextCount(1)
                .verifyComplete();
        StepVerifier.create(service.handle(new ReactionEvent.ReactionAddedEvent(domainId, domainObjectType, domainObjectId, userId2, "LIKE")))
                .expectNextCount(1)
                .verifyComplete();
        StepVerifier.create(service.handle(new ReactionEvent.ReactionRemovedEvent(domainId, domainObjectType, domainObjectId, userId2, "LIKE")))
                .expectNextCount(1)
                .verifyComplete();
        StepVerifier.create(service.handle(new ReactionEvent.ReactionAddedEvent(domainId, domainObjectType, domainObjectId, userId3, "DISLIKE")))
                .expectNextCount(1)
                .verifyComplete();
        StepVerifier.create(
                        queryService.fetchBy(domainObjectType, domainObjectId)
                ).assertNext(userRating -> {
                    assertThat(userRating.getReactions()).hasSize(2);
                    assertThat(userRating.getReactions().get("LIKE")).isEqualTo(1);
                    assertThat(userRating.getReactions().get("DISLIKE")).isEqualTo(1);
                })
                .verifyComplete();
    }
}