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
    public static final String resourcePath = "/api/v1/" + resourceName;
    private final ReactionService service;

    @PostMapping
    Mono<ResponseEntity<Reaction>> add(
            @RequestBody ReactionCommand.AddReactionCommand command
    ) {
        return service.add(command)
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }

    @DeleteMapping
    Mono<ResponseEntity<Reaction>> remove(
            @RequestBody ReactionCommand.RemoveReactionCommand command
    ) {
        return service.remove(command)
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }
}
