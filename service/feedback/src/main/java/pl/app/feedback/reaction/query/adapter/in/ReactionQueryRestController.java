package pl.app.feedback.reaction.query.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.app.feedback.reaction.query.dto.ReactionDto;
import pl.app.feedback.reaction.query.port.DomainObjectReactionQueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ReactionQueryRestController.resourcePath)
@RequiredArgsConstructor
class ReactionQueryRestController {
    public static final String resourceName = "reactions";
    public static final String resourcePath = "/api/v1/" + resourceName;
    private final DomainObjectReactionQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Flux<ReactionDto>>> fetchAllBy(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String domainObjectType,
            @RequestParam(required = false) String domainObjectId,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer pageSize
    ) {
        return Mono.just(ResponseEntity.ok(queryService.fetchAllBy(userId, domainObjectType, domainObjectId, cursor, pageSize)));
    }
}
