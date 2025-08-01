package pl.app.feedback.rating.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.feedback.rating.application.domain.model.Rating;
import pl.app.feedback.rating.application.port.in.RatingCommand;
import pl.app.feedback.rating.application.port.in.RatingService;
import reactor.core.publisher.Mono;

//TODO get userId from token
@RestController
@RequestMapping(RatingRestController.resourcePath)
@RequiredArgsConstructor
class RatingRestController {
    public static final String resourceName = "ratings";
    public static final String resourcePath = "/api/v1/" + resourceName;
    private final RatingService service;

    @PostMapping
    Mono<ResponseEntity<Rating>> create(
            @RequestBody RatingCommand.CrateRatingCommand command
    ) {
        return service.create(command)
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }

    @DeleteMapping
    Mono<ResponseEntity<Rating>> remove(
            @RequestBody RatingCommand.RemoveRatingCommand command
    ) {
        return service.remove(command)
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }
}
