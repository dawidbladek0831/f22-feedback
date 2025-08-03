package pl.app.feedback.rating.application.port.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import pl.app.common.event.EventPublisher;
import pl.app.feedback.rating.application.domain.model.RatingEvent;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class RatingServiceTest {
    @Autowired
    private RatingService service;

    @MockitoSpyBean
    private EventPublisher eventPublisher;

    @Test
    void whenCreateCommandIsSend_thenRatingShouldBeCrated() {
        var domainObjectType = "POST";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var rating = 5d;

        StepVerifier.create(
                service.upsert(new RatingCommand.UpsertRatingCommand(domainObjectType, domainObjectId, userId, rating))
        ).assertNext(domain -> {
            assertThat(domain).isNotNull();
            assertThat(domain.getDomainObjectType()).isEqualTo(domainObjectType);
            assertThat(domain.getDomainObjectId()).isEqualTo(domainObjectId);
            assertThat(domain.getUserId()).isEqualTo(userId);
            assertThat(domain.getRating()).isEqualTo(rating);

            verify(eventPublisher, times(1)).publish(ArgumentMatchers.eq(
                    new RatingEvent.RatingCreatedEvent(domain.getId(), domainObjectType, domainObjectId, userId, rating)
            ));
        }).verifyComplete();
    }
    @Test
    void whenCreateCommandIsSendSecondTime_thenRatingShouldBeUpdated() {
        var domainObjectType = "POST";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var firstRating = 15d;
        var secondRating = 5d;

        service.upsert(new RatingCommand.UpsertRatingCommand(domainObjectType, domainObjectId, userId, firstRating)).block();

        Mockito.reset(eventPublisher);

        StepVerifier.create(
                service.upsert(new RatingCommand.UpsertRatingCommand(domainObjectType, domainObjectId, userId, secondRating))
        ).assertNext(domain -> {
            assertThat(domain).isNotNull();
            assertThat(domain.getRating()).isEqualTo(secondRating);

            verify(eventPublisher, times(1)).publish(ArgumentMatchers.eq(
                    new RatingEvent.RatingUpdatedEvent(domain.getId(), domainObjectType, domainObjectId, userId, secondRating, firstRating)
            ));
        }).verifyComplete();
    }
}