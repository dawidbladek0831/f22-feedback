package pl.app.feedback.rating.query.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.feedback.rating.application.domain.model.Rating;
import pl.app.feedback.rating.query.port.DomainObjectRatingQueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(RatingQueryRestController.resourcePath)
@RequiredArgsConstructor
class RatingQueryRestController {
    public static final String resourceName = "ratings";
    public static final String resourcePath = "/api/v1/" + resourceName;
    private final DomainObjectRatingQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Flux<Rating>>> fetchAllBy(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String domainObjectType,
            @RequestParam(required = false) String domainObjectId
    ) {
        return Mono.just(ResponseEntity.ok(queryService.fetchAllBy(userId, domainObjectType, domainObjectId)));
    }

}
