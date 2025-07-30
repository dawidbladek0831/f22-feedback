package pl.app.feedback.reaction.query.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.feedback.reaction.application.domain.model.ReactionEvent;

import java.util.*;

@Document(collection = "user-reaction")
@Data
@NoArgsConstructor
public class UserReaction {
    @Id
    private ObjectId id;
    private String userId;
    private List<Reaction> reactions;
    @Version
    private Long version;

    public UserReaction(String userId) {
        this.id = ObjectId.get();
        this.userId = userId;
        this.reactions = new ArrayList<>();
        this.version = null;
    }

    public void handle(ReactionEvent.ReactionCreatedEvent event) {
        getReaction(event.id()).ifPresentOrElse(
                reaction -> {
                },
                () -> {
                    var reaction = new UserReaction.Reaction(event.domainObjectType(), event.domainObjectId(), userId);
                    reactions.add(reaction);
                }
        );
    }

    public void handle(ReactionEvent.ReactionAddedEvent event) {
        getReaction(event.id()).ifPresentOrElse(
                reaction -> {
                    reaction.getReactions().add(event.reaction());
                },
                () -> {
                    var reaction = new UserReaction.Reaction(event.domainObjectType(), event.domainObjectId(), userId);
                    reaction.getReactions().add(event.reaction());
                    reactions.add(reaction);
                }
        );
    }

    public void handle(ReactionEvent.ReactionRemovedEvent event) {
        getReaction(event.id()).ifPresentOrElse(
                reaction -> {
                    reaction.getReactions().remove(event.reaction());
                },
                () -> {
                }
        );
    }

    private Optional<Reaction> getReaction(ObjectId reactionId) {
        return reactions.stream()
                .filter(r -> r.getId().equals(reactionId))
                .findFirst();
    }

    @Data
    @NoArgsConstructor
    static public class Reaction {
        private ObjectId id;
        private String domainObjectType;
        private String domainObjectId;
        private String userId;
        private Set<String> reactions;

        public Reaction(String domainObjectType, String domainObjectId, String userId) {
            this.id = ObjectId.get();
            this.domainObjectType = domainObjectType;
            this.domainObjectId = domainObjectId;
            this.userId = userId;
            this.reactions = new HashSet<>();
        }
    }
}
