package pl.app.feedback.reaction.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.feedback.reaction.application.domain.model.Reaction;
import pl.app.feedback.reaction.application.port.in.ReactionCommand;
import pl.app.feedback.reaction.application.port.in.ReactionService;
import reactor.core.publisher.Mono;

//TODO get userId from token
@RestController
@RequestMapping(ReactionRestController.resourcePath)
@RequiredArgsConstructor
class ReactionRestController {
    public static final String resourceName = "reactions";
    public static final String resourcePath = "/api/v1/users/{userId}/objects/{domainObjectType}/{domainObjectId}/" + resourceName;
    private final ReactionService service;

    @PostMapping("/{reaction}")
    Mono<ResponseEntity<Reaction>> add(
            @PathVariable String userId,
            @PathVariable String domainObjectType,
            @PathVariable String domainObjectId,
            @PathVariable String reaction
    ) {
        return service.add(new ReactionCommand.AddReactionCommand(domainObjectType, domainObjectId, userId, reaction))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }

    @DeleteMapping("/{reaction}")
    Mono<ResponseEntity<Reaction>> remove(
            @PathVariable String userId,
            @PathVariable String domainObjectType,
            @PathVariable String domainObjectId,
            @PathVariable String reaction
    ) {
        return service.remove(new ReactionCommand.RemoveReactionCommand(domainObjectType, domainObjectId, userId, reaction))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }
}
