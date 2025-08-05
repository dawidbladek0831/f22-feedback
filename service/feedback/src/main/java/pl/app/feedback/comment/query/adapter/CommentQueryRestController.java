package pl.app.feedback.comment.query.adapter;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.app.feedback.comment.application.domain.model.Comment;
import pl.app.feedback.comment.query.port.CommentQueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(CommentQueryRestController.resourcePath)
@RequiredArgsConstructor
class CommentQueryRestController {
    public static final String resourceName = "comments";
    public static final String resourcePath = "/api/v1/" + resourceName;
    private final CommentQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Flux<Comment>>> fetchAllBy(
            @RequestParam(required = false) String domainObjectType,
            @RequestParam(required = false) String domainObjectId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) ObjectId parentId,
            @RequestParam(required = false) Comment.Status status,

            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer pageSize
    ) {
        return Mono.just(ResponseEntity.ok(queryService.fetchAllBy(domainObjectType, domainObjectId, userId, parentId, status, cursor, pageSize)));
    }

}
