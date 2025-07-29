package pl.app.feedback.rating.query.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.feedback.rating.application.domain.model.Rating;
import pl.app.feedback.rating.query.port.DomainObjectRatingQueryService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(RatingQueryRestController.resourcePath)
@RequiredArgsConstructor
class RatingQueryRestController {
    public static final String resourceName = "ratings";
    public static final String resourcePath = "/api/v1/users/{userId}/objects/{domainObjectType}/{domainObjectId}/" + resourceName;
    private final DomainObjectRatingQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Rating>> fetchBy(
            @PathVariable String userId,
            @PathVariable String domainObjectType,
            @PathVariable String domainObjectId
    ) {
        return queryService.fetchBy(userId, domainObjectType, domainObjectId)
                .map(ResponseEntity::ok);
    }
}
