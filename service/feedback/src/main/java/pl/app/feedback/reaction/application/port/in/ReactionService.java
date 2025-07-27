package pl.app.feedback.reaction.application.port.in;

import pl.app.feedback.reaction.application.domain.UserReaction;
import reactor.core.publisher.Mono;

public interface ReactionService {
    Mono<UserReaction> add(ReactionCommand.AddUserReactionCommand command);

    Mono<UserReaction> remove(ReactionCommand.RemoveUserReactionCommand command);
}