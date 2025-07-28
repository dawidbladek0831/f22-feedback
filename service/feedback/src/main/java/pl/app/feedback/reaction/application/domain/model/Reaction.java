package pl.app.feedback.reaction.application.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "reaction")
@Getter
@NoArgsConstructor
public class Reaction {
    @Id
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
        this.reactions = new HashSet<>(1);
    }

    public void addReaction(String newReaction) {
        if (containReaction(newReaction)) {
            throw ReactionException.DuplicatedReactionException.reaction(newReaction);
        }
        this.reactions.add(newReaction);
    }

    public void removeReaction(String reaction) {
        if (!containReaction(reaction)) {
            throw ReactionException.NotFoundReactionException.reaction(reaction);
        }
        this.reactions.remove(reaction);
    }

    public Set<String> removeReactionAll() {
        var reactionsToRemove = new HashSet<>(this.reactions);
        this.reactions.clear();
        return reactionsToRemove;
    }

    public boolean containReaction(String reaction) {
        return this.reactions.contains(reaction);
    }
}
