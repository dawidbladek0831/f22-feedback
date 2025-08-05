package pl.app.feedback.comment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.feedback.comment.application.domain.model.Comment;
import pl.app.feedback.comment.application.port.in.CommentCommand;
import pl.app.feedback.comment.application.port.in.CommentService;
import pl.app.feedback.comment.query.port.CommentQueryService;
import pl.app.feedback.config.AuthorizationService;
import pl.app.feedback.config.SecurityScopes;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(CommentRestController.resourcePath)
@RequiredArgsConstructor
class CommentRestController {
    public static final String resourceName = "comments";
    public static final String resourcePath = "/api/v1/" + resourceName;
    private final CommentService service;
    private final CommentQueryService queryService;

    @PostMapping
    Mono<ResponseEntity<Comment>> create(
            @RequestBody CommentCommand.CreateCommentCommand command
    ) {
        return AuthorizationService.verifySubjectIsOwnerOrHasAuthority(command.userId(), SecurityScopes.COMMENT_MANAGE.getScopeName())
                .then(service.create(command))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }

    @PatchMapping("/{commentId}")
    Mono<ResponseEntity<Comment>> update(
            @PathVariable ObjectId commentId,
            @RequestBody CommentCommand.UpdateCommentCommand command
    ) {
        return queryService.fetchBy(command.commentId())
                .flatMap(comment -> AuthorizationService.verifySubjectIsOwnerOrHasAuthority(comment.getUserId(), SecurityScopes.COMMENT_MANAGE.getScopeName()))
                .then(service.update(command))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }

    @DeleteMapping("/{commentId}")
    Mono<ResponseEntity<Comment>> remove(
            @PathVariable ObjectId commentId
    ) {
        return queryService.fetchBy(commentId)
                .flatMap(comment -> AuthorizationService.verifySubjectIsOwnerOrHasAuthority(comment.getUserId(), SecurityScopes.COMMENT_MANAGE.getScopeName()))
                .then(service.remove(new CommentCommand.RemoveCommentCommand(commentId)))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }

    @PostMapping("/{commentId}/hides")
    Mono<ResponseEntity<Comment>> hide(
            @PathVariable ObjectId commentId
    ) {
        return service.hide(new CommentCommand.HideCommentCommand(commentId))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }

    @PostMapping("/{commentId}/restorations")
    Mono<ResponseEntity<Comment>> restore(
            @PathVariable ObjectId commentId
    ) {
        return service.restore(new CommentCommand.RestoreCommentCommand(commentId))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }
}
