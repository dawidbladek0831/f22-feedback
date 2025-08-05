package pl.app.feedback.rating.query.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.feedback.rating.query.dto.RatingDto;
import pl.app.feedback.rating.query.port.DomainObjectRatingQueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(UserRatingQueryRestController.resourcePath)
@RequiredArgsConstructor
class UserRatingQueryRestController {
    public static final String resourceName = "ratings";
    public static final String resourcePath = "/api/v1/users/{userId}/" + resourceName;

    private final DomainObjectRatingQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Flux<RatingDto>>> fetchBy(
            @PathVariable String userId
    ) {
        return Mono.just(ResponseEntity.ok(queryService.fetchUserRating(userId)));
    }
}
