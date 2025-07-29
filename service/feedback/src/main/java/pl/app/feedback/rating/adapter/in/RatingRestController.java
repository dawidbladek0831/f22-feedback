package pl.app.feedback.rating.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.feedback.rating.application.domain.model.Rating;
import pl.app.feedback.rating.application.port.in.RatingCommand;
import pl.app.feedback.rating.application.port.in.RatingService;
import pl.app.feedback.reaction.application.domain.model.Reaction;
import pl.app.feedback.reaction.application.port.in.ReactionCommand;
import reactor.core.publisher.Mono;

//TODO get userId from token
@RestController
@RequestMapping(RatingRestController.resourcePath)
@RequiredArgsConstructor
class RatingRestController {
    public static final String resourceName = "ratings";
    public static final String resourcePath = "/api/v1/users/{userId}/objects/{domainObjectType}/{domainObjectId}/" + resourceName;
    private final RatingService service;

    @PostMapping("/{rating}")
    Mono<ResponseEntity<Rating>> create(
            @PathVariable String userId,
            @PathVariable String domainObjectType,
            @PathVariable String domainObjectId,
            @PathVariable Double rating
    ) {
        return service.create(new RatingCommand.CrateRatingCommand(domainObjectType, domainObjectId, userId, rating))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }

    @DeleteMapping
    Mono<ResponseEntity<Rating>> remove(
            @PathVariable String userId,
            @PathVariable String domainObjectType,
            @PathVariable String domainObjectId
    ) {
        return service.remove(new RatingCommand.RemoveRatingCommand(domainObjectType, domainObjectId, userId))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }
}
