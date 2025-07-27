package pl.app.feedback.reaction.application.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Document(collection = "user_reaction")
@Getter
@NoArgsConstructor
public class UserReaction {
    @Id
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;

    private String userId;

    private Set<String> reactions;

    public UserReaction(String domainObjectType, String domainObjectId, String userId) {
        this.id = ObjectId.get();
        this.domainObjectType = domainObjectType;
        this.domainObjectId = domainObjectId;
        this.userId = userId;
        this.reactions = new HashSet<>(1);
    }

    public void addReaction(String newReaction) {
        if(containReaction(newReaction)){
            throw ReactionException.DuplicatedReactionException.reaction(newReaction);
        }
        this.reactions.add(newReaction);
    }

    public void removeReaction(String reaction) {
        if(containReaction(reaction)){
            throw ReactionException.NotFoundUserReactionException.reaction(reaction);
        }
        this.reactions.remove(reaction);
    }
    public Set<String> removeReactionAll() {
        var reactionsToRemove = new HashSet<>(this.reactions);
        this.reactions.clear();
        return reactionsToRemove;
    }

    public boolean containReaction(String reaction){
        return this.reactions.contains(reaction);
    }
}
