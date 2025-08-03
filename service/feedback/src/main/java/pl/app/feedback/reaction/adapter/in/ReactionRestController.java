package pl.app.feedback.reaction.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.feedback.reaction.application.domain.model.Reaction;
import pl.app.feedback.reaction.application.port.in.ReactionCommand;
import pl.app.feedback.reaction.application.port.in.ReactionService;
import pl.app.feedback.reaction.query.dto.ReactionDto;
import pl.app.feedback.reaction.query.service.ReactionMapper;
import reactor.core.publisher.Mono;

//TODO get userId from token
@RestController
@RequestMapping(ReactionRestController.resourcePath)
@RequiredArgsConstructor
class ReactionRestController {
    public static final String resourceName = "reactions";
    public static final String resourcePath = "/api/v1/" + resourceName;
    private final ReactionService service;
    private final ReactionMapper mapper;

    @PostMapping
    Mono<ResponseEntity<ReactionDto>> add(
            @RequestBody ReactionCommand.AddReactionCommand command
    ) {
        return service.add(command)
                .map(e -> mapper.map(e, ReactionDto.class))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }

    @DeleteMapping
    Mono<ResponseEntity<ReactionDto>> remove(
            @RequestBody ReactionCommand.RemoveReactionCommand command
    ) {
        return service.remove(command)
                .map(e -> mapper.map(e, ReactionDto.class))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }
}
