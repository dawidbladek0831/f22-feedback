package pl.app.feedback.rating.application.port.in;

import pl.app.feedback.rating.application.domain.model.Rating;
import reactor.core.publisher.Mono;

public interface RatingService {
    Mono<Rating> create(RatingCommand.CrateRatingCommand command);

    Mono<Rating> remove(RatingCommand.RemoveRatingCommand command);
}