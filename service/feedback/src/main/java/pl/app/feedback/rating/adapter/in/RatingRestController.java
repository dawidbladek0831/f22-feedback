package pl.app.feedback.rating.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.feedback.config.AuthorizationService;
import pl.app.feedback.config.SecurityScopes;
import pl.app.feedback.rating.application.port.in.RatingCommand;
import pl.app.feedback.rating.application.port.in.RatingService;
import pl.app.feedback.rating.query.dto.RatingDto;
import pl.app.feedback.rating.query.service.RatingMapper;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(RatingRestController.resourcePath)
@RequiredArgsConstructor
class RatingRestController {
    public static final String resourceName = "ratings";
    public static final String resourcePath = "/api/v1/" + resourceName;
    private final RatingService service;
    private final RatingMapper mapper;

    @PutMapping
    Mono<ResponseEntity<RatingDto>> upsert(
            @RequestBody RatingCommand.UpsertRatingCommand command
    ) {
        return AuthorizationService.verifySubjectIsOwnerOrHasAuthority(command.userId(), SecurityScopes.RATING_MANAGE.getScopeName())
                .then(service.upsert(command))
                .map(e -> mapper.map(e, RatingDto.class))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }

    @DeleteMapping
    Mono<ResponseEntity<RatingDto>> remove(
            @RequestBody RatingCommand.RemoveRatingCommand command
    ) {
        return AuthorizationService.verifySubjectIsOwnerOrHasAuthority(command.userId(), SecurityScopes.RATING_MANAGE.getScopeName())
                .then(service.remove(command))
                .map(e -> mapper.map(e, RatingDto.class))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }
}
