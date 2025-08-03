package pl.app.feedback.comment.query.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.feedback.comment.query.model.DomainObjectComment;
import pl.app.feedback.comment.query.port.DomainObjectCommentQueryService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(DomainObjectCommentQueryRestController.resourcePath)
@RequiredArgsConstructor
class DomainObjectCommentQueryRestController {
    public static final String resourceName = "comments";
    public static final String resourcePath = "/api/v1/objects/{domainObjectType}/{domainObjectId}/" + resourceName;
    private final DomainObjectCommentQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Mono<DomainObjectComment>>> fetchAllBy(
            @PathVariable String domainObjectType,
            @PathVariable String domainObjectId
    ) {
        return Mono.just(ResponseEntity.ok(queryService.fetchAllBy(domainObjectType, domainObjectId)));
    }
}
