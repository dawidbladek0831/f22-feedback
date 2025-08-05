package pl.app.feedback.rating.adapter.out;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.app.feedback.rating.application.domain.model.RatingEvent;
import pl.app.feedback.rating.query.port.DomainObjectRatingQueryService;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class DomainObjectRatingReadModelSynchronizerTest {
    @Autowired
    private DomainObjectRatingReadModelSynchronizer service;

    @Autowired
    private DomainObjectRatingQueryService queryService;

    @Test
    void testDomainObjectRatingReadModelAfterCreate() {
        var domainId = ObjectId.get();
        var domainObjectType = "POST";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var firstRating = 15d;
        var createEvent = new RatingEvent.RatingCreatedEvent(domainId, domainObjectType, domainObjectId, userId, firstRating);

        StepVerifier.create(service.handle(createEvent))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(
                        queryService.fetchDomainObjectRating(domainObjectType, domainObjectId)
                ).assertNext(userRating -> {
                    assertThat(userRating.getQuantity()).isEqualTo(1);
                    assertThat(userRating.getRating()).isEqualTo(firstRating);
                    assertThat(userRating.getSum()).isEqualTo(firstRating);
                })
                .verifyComplete();
    }

    @Test
    void testDomainObjectRatingReadModelAfterUpdate() {
        var domainId = ObjectId.get();
        var domainObjectType = "POST";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var firstRating = 15d;
        var secondRating = 5d;
        var createEvent = new RatingEvent.RatingCreatedEvent(domainId, domainObjectType, domainObjectId, userId, firstRating);
        var updateEvent = new RatingEvent.RatingUpdatedEvent(domainId, domainObjectType, domainObjectId, userId, secondRating, firstRating);

        StepVerifier.create(service.handle(createEvent))
                .expectNextCount(1)
                .verifyComplete();
        StepVerifier.create(service.handle(updateEvent))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(
                        queryService.fetchDomainObjectRating(domainObjectType, domainObjectId)
                ).assertNext(userRating -> {
                    assertThat(userRating.getQuantity()).isEqualTo(1);
                    assertThat(userRating.getRating()).isEqualTo(secondRating);
                    assertThat(userRating.getSum()).isEqualTo(secondRating);
                })
                .verifyComplete();
    }

    @Test
    void testDomainObjectRatingReadModelAfterRemove() {
        var domainId = ObjectId.get();
        var domainObjectType = "POST";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var firstRating = 15d;
        var secondRating = 5d;
        var createEvent = new RatingEvent.RatingCreatedEvent(domainId, domainObjectType, domainObjectId, userId, firstRating);
        var updateEvent = new RatingEvent.RatingUpdatedEvent(domainId, domainObjectType, domainObjectId, userId, secondRating, firstRating);
        var removeEvent = new RatingEvent.RatingRemovedEvent(domainId, domainObjectType, domainObjectId, userId, secondRating);

        StepVerifier.create(service.handle(createEvent))
                .expectNextCount(1)
                .verifyComplete();
        StepVerifier.create(service.handle(updateEvent))
                .expectNextCount(1)
                .verifyComplete();
        StepVerifier.create(service.handle(removeEvent))
                .expectNextCount(1)
                .verifyComplete();
        StepVerifier.create(
                        queryService.fetchDomainObjectRating(domainObjectType, domainObjectId)
                ).assertNext(userRating -> {
                    assertThat(userRating.getQuantity()).isEqualTo(0);
                    assertThat(userRating.getRating()).isEqualTo(0);
                    assertThat(userRating.getSum()).isEqualTo(0);
                })
                .verifyComplete();
    }

    @Test
    void testDomainObjectRatingReadModelAfterTwoCreates() {
        var domainId = ObjectId.get();
        var domainObjectType = "POST";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var userId2 = ObjectId.get().toString();
        var firstRating = 15d;
        var firstRating2 = 17d;
        var createEvent = new RatingEvent.RatingCreatedEvent(domainId, domainObjectType, domainObjectId, userId, firstRating);
        var createEvent2 = new RatingEvent.RatingCreatedEvent(domainId, domainObjectType, domainObjectId, userId2, firstRating2);

        StepVerifier.create(service.handle(createEvent))
                .expectNextCount(1)
                .verifyComplete();
        StepVerifier.create(service.handle(createEvent2))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(
                        queryService.fetchDomainObjectRating(domainObjectType, domainObjectId)
                ).assertNext(userRating -> {
                    assertThat(userRating.getQuantity()).isEqualTo(2);
                    assertThat(userRating.getRating()).isEqualTo((firstRating + firstRating2) / 2);
                    assertThat(userRating.getSum()).isEqualTo(firstRating + firstRating2);
                })
                .verifyComplete();
    }
}