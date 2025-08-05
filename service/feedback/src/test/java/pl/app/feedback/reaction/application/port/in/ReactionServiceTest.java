package pl.app.feedback.reaction.application.port.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import pl.app.common.event.EventPublisher;
import pl.app.feedback.reaction.application.domain.model.ReactionEvent;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Autowired
    private ReactionService reactionService;

    @MockitoSpyBean
    private EventPublisher eventPublisher;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("app.reaction.policy.allowed-domain-object-types-policy.enable", () -> "true");
        registry.add("app.reaction.policy.allowed-domain-object-types-policy.types", () -> "POST,VIDEO");

        registry.add("app.reaction.policy.allowed-reactions-policy.enable", () -> "true");
        registry.add("app.reaction.policy.allowed-reactions-policy.default-reactions", () -> "LIKE,DISLIKE");
        registry.add("app.reaction.policy.allowed-reactions-policy.types.VIDEO.reactions", () -> "BORING,GREAT,AMAZING");

        registry.add("app.reaction.policy.single-reaction-policy.enable", () -> "true");
        registry.add("app.reaction.policy.single-reaction-policy.types.VIDEO.enable", () -> "false");
    }

    @Test
    void whenUserSendFirstReaction_thenReactionIsCratedAndUpdated() {
        var domainObjectType = "poST";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var reaction = "LIke";

        StepVerifier.create(
                reactionService.add(new ReactionCommand.AddReactionCommand(domainObjectType, domainObjectId, userId, reaction))
        ).assertNext(domain -> {
            assertThat(domain).isNotNull();
            assertThat(domain.getDomainObjectType()).isEqualTo(domainObjectType.toUpperCase());
        }).verifyComplete();
        verify(eventPublisher, times(1)).publish(any(ReactionEvent.ReactionCreatedEvent.class));
        verify(eventPublisher, times(1)).publish(any(ReactionEvent.ReactionAddedEvent.class));
    }

    @Test
    void whenUserSendSecondReaction_thenReactionShouldBeOnlyUpdated() {
        var domainObjectType = "poST";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var reaction = "LIke";
        reactionService.add(new ReactionCommand.AddReactionCommand(domainObjectType, domainObjectId, userId, reaction)).block();

        Mockito.reset(eventPublisher);

        StepVerifier.create(
                reactionService.add(new ReactionCommand.AddReactionCommand(domainObjectType, domainObjectId, userId, reaction))
        ).assertNext(domain -> {
            assertThat(domain).isNotNull();
        }).verifyComplete();
        verify(eventPublisher, times(0)).publish(any(ReactionEvent.ReactionCreatedEvent.class));
        verify(eventPublisher, times(1)).publish(any(ReactionEvent.ReactionAddedEvent.class));
    }

    @Test
    void whenCommandContainsNotAllowedDomainObjectType_thenAllowedDomainObjectTypesPolicyShouldThrowException() {
        var domainObjectType = "ARTICLE";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var reaction = "LIKE";

        StepVerifier.create(
                reactionService.add(new ReactionCommand.AddReactionCommand(domainObjectType, domainObjectId, userId, reaction))
        ).verifyError();
    }

    @Test
    void whenCommandContainsAllowedReactionOnType_thenAllowedUserReactionsPolicyShouldPassCommand() {
        var domainObjectType = "VIDEO";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var reaction = "AMAZING";

        StepVerifier.create(
                reactionService.add(new ReactionCommand.AddReactionCommand(domainObjectType, domainObjectId, userId, reaction))
        ).assertNext(domain -> {
            assertThat(domain).isNotNull();
        }).verifyComplete();
    }

    @Test
    void whenCommandContainsNotAllowedReaction_thenAllowedUserReactionsPolicyShouldThrowException() {
        var domainObjectType = "POST";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var reaction = "BORING";

        StepVerifier.create(
                reactionService.add(new ReactionCommand.AddReactionCommand(domainObjectType, domainObjectId, userId, reaction))
        ).verifyError();
    }

    @Test
    void whenSingleUserReactionPolicyIsEnable_thenBeforeAddingReactionPolicyShouldRemoveExistingReactions() {
        var domainObjectType = "POST";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var reaction = "LIKE";
        reactionService.add(new ReactionCommand.AddReactionCommand(domainObjectType, domainObjectId, userId, reaction)).block();

        Mockito.reset(eventPublisher);

        StepVerifier.create(
                reactionService.add(new ReactionCommand.AddReactionCommand(domainObjectType, domainObjectId, userId, "DISLIKE"))
        ).assertNext(domain -> {
            assertThat(domain).isNotNull();
            assertThat(domain.getReactions()).hasSize(1);
        }).verifyComplete();

        verify(eventPublisher, times(1)).publish(any(ReactionEvent.ReactionAddedEvent.class));
        verify(eventPublisher, times(1)).publish(any(ReactionEvent.ReactionRemovedEvent.class));
    }

    @Test
    void whenUserRemoveExistingReaction_thenReactionShouldBeRemoved() {
        var domainObjectType = "POST";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var reaction = "LIKE";
        reactionService.add(new ReactionCommand.AddReactionCommand(domainObjectType, domainObjectId, userId, reaction)).block();
        Mockito.reset(eventPublisher);

        StepVerifier.create(
                reactionService.remove(new ReactionCommand.RemoveReactionCommand(domainObjectType, domainObjectId, userId, reaction))
        ).assertNext(domain -> {
            assertThat(domain).isNotNull();
        }).verifyComplete();

        verify(eventPublisher, times(1)).publish(any(ReactionEvent.ReactionRemovedEvent.class));
    }
}