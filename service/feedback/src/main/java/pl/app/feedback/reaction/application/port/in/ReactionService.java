package pl.app.feedback.reaction.application.port.in;

import pl.app.feedback.reaction.application.domain.model.Reaction;
import reactor.core.publisher.Mono;

public interface ReactionService {
    Mono<Reaction> add(ReactionCommand.AddReactionCommand command);

    Mono<Reaction> remove(ReactionCommand.RemoveReactionCommand command);
}