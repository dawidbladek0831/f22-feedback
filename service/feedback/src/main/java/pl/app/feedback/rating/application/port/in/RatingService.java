package pl.app.feedback.rating.application.port.in;

import pl.app.feedback.rating.application.domain.model.Rating;
import reactor.core.publisher.Mono;

public interface RatingService {
    Mono<Rating> upsert(RatingCommand.UpsertRatingCommand command);

    Mono<Rating> remove(RatingCommand.RemoveRatingCommand command);
}