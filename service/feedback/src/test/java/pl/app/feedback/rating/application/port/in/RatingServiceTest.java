package pl.app.feedback.rating.application.port.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("app.reaction.policy.allowed-domain-object-types-rating-policy.enable", () -> "true");
        registry.add("app.reaction.policy.allowed-domain-object-types-rating-policy.types", () -> "POST,VIDEO");

        registry.add("app.reaction.policy.range-rating-policy.enable", () -> "true");
        registry.add("app.reaction.policy.range-rating-policy.defaultMin", () -> "0");
        registry.add("app.reaction.policy.range-rating-policy.defaultMax", () -> "5");
        registry.add("app.reaction.policy.range-rating-policy.types.VIDEO.min", () -> "-10");
        registry.add("app.reaction.policy.range-rating-policy.types.VIDEO.max", () -> "10");
    }

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
        var firstRating = 3d;
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

    @Test
    void whenCommandContainsAllowedReactionOnType_thenAllowedDomainObjectTypesRatingPolicyShouldPassCommand() {
        var domainObjectType = "VIDEO";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var firstRating = 3d;

        StepVerifier.create(
                service.upsert(new RatingCommand.UpsertRatingCommand(domainObjectType, domainObjectId, userId, firstRating))
        ).assertNext(domain -> {
            assertThat(domain).isNotNull();
        }).verifyComplete();
    }

    @Test
    void whenCommandContainsNotAllowedReaction_thenAllowedDomainObjectTypesRatingPolicyShouldThrowException() {
        var domainObjectType = "ARTICLE";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var firstRating = 3d;

        StepVerifier.create(
                service.upsert(new RatingCommand.UpsertRatingCommand(domainObjectType, domainObjectId, userId, firstRating))
        ).verifyError();
    }

    @Test
    void whenRatingIsOutOfRange_thenRangeRatingPolicyShouldThrowException() {
        var domainObjectType = "POST";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var firstRating = 30000d;

        StepVerifier.create(
                service.upsert(new RatingCommand.UpsertRatingCommand(domainObjectType, domainObjectId, userId, firstRating))
        ).verifyError();
    }
}