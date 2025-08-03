package pl.app.feedback.reaction.query.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.feedback.reaction.query.dto.ReactionDto;
import pl.app.feedback.reaction.query.port.DomainObjectReactionQueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(UserReactionQueryRestController.resourcePath)
@RequiredArgsConstructor
class UserReactionQueryRestController {
    public static final String resourceName = "reactions";
    public static final String resourcePath = "/api/v1/users/{userId}/" + resourceName;

    private final DomainObjectReactionQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Flux<ReactionDto>>> fetchBy(
            @PathVariable String userId
    ) {
        return Mono.just(ResponseEntity.ok(queryService.fetchAllUserReaction(userId)));
    }

}
