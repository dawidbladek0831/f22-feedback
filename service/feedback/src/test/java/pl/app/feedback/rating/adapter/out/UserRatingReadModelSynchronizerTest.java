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
class UserRatingReadModelSynchronizerTest {
    @Autowired
    private UserRatingReadModelSynchronizer service;

    @Autowired
    private DomainObjectRatingQueryService queryService;

    @Test
    void testUserRatingReadModelAfterCreate() {
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

        StepVerifier.create(
                        queryService.fetchUserRating(userId)
                ).assertNext(rating -> {
                    assertThat(rating.getId()).isEqualTo(domainId);
                    assertThat(rating.getDomainObjectType()).isEqualTo(domainObjectType);
                    assertThat(rating.getDomainObjectId()).isEqualTo(domainObjectId);
                    assertThat(rating.getUserId()).isEqualTo(userId);
                    assertThat(rating.getRating()).isEqualTo(firstRating);
                })
                .verifyComplete();
    }

    @Test
    void testUserRatingReadModelAfterUpdate() {
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
                        queryService.fetchUserRating(userId)
                ).assertNext(rating -> {
                    assertThat(rating.getId()).isEqualTo(domainId);
                    assertThat(rating.getDomainObjectType()).isEqualTo(domainObjectType);
                    assertThat(rating.getDomainObjectId()).isEqualTo(domainObjectId);
                    assertThat(rating.getUserId()).isEqualTo(userId);
                    assertThat(rating.getRating()).isEqualTo(secondRating);
                })
                .verifyComplete();
    }

    @Test
    void testUserRatingReadModelAfterRemove() {
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
                        queryService.fetchUserRating(userId)
                )
                .verifyComplete();
    }

}