package pl.app.feedback.reaction.adapter.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ReactionRestControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenCommandIsValid_thenShouldAddReaction() {
        var domainObjectType = "POST";
        var domainObjectId = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var reaction = "LIKE";

        webTestClient.post().uri(ReactionRestController.resourcePath + "/{reaction}", userId, domainObjectType, domainObjectId, reaction)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

}