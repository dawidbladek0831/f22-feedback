package pl.app.feedback.reaction.query.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.feedback.reaction.query.model.DomainObjectReaction;
import pl.app.feedback.reaction.query.port.DomainObjectReactionQueryService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(DomainObjectReactionQueryRestController.resourcePath)
@RequiredArgsConstructor
class DomainObjectReactionQueryRestController {
    public static final String resourceName = "reactions";
    public static final String resourcePath = "/api/v1/objects/{domainObjectType}/{domainObjectId}/" + resourceName;

    private final DomainObjectReactionQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<DomainObjectReaction>> fetchBy(
            @PathVariable String domainObjectType,
            @PathVariable String domainObjectId
    ) {
        return queryService.fetchBy(domainObjectType, domainObjectId)
                .map(ResponseEntity::ok);
    }
}
