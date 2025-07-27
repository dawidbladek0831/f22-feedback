package pl.app.feedback.reaction.application.port.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ReactionService reactionService;

    @Test
    void whenUserSendFirstReaction_thenReactionIsCratedAndUpdated() {
        var domainObjectType = "PoST";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var reaction = "LIkE";

        StepVerifier.create(
                reactionService.add(new ReactionCommand.AddUserReactionCommand(domainObjectType, domainObjectId, userId, reaction))
        ).assertNext(domain ->{
            System.out.println(domain);
            assertThat(domain).isNotNull();
            assertThat(domain.getDomainObjectType()).isEqualTo(domainObjectType.toUpperCase());
        }).verifyComplete();
    }
}