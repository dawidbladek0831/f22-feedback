package pl.app.feedback.reaction.query.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Document(collection = "reaction")
@Getter
@NoArgsConstructor
public class DomainObjectReaction {
    @Id
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;

    private Set<ReactionCounter> reactionCounters;

    public DomainObjectReaction(String domainObjectType, String domainObjectId) {
        this.id = ObjectId.get();
        this.domainObjectType = domainObjectType;
        this.domainObjectId = domainObjectId;
        this.reactionCounters = new HashSet<>();
    }

    public void increment(String reaction) {
        getReactionCounter(reaction).ifPresent(ReactionCounter::increment);
    }

    public void decrement(String reaction) {
        getReactionCounter(reaction).ifPresent(ReactionCounter::decrement);
    }

    public Optional<ReactionCounter> getReactionCounter(String reaction) {
        return this.reactionCounters.stream().filter(rc -> rc.getReaction().equals(reaction)).findAny();
    }

    @Getter
    public static class ReactionCounter {
        private String reaction;
        private Long number;

        @SuppressWarnings("unused")
        public ReactionCounter() {
        }

        public ReactionCounter(String type) {
            this.reaction = type;
            this.number = 0L;
        }

        public void increment() {
            this.number++;
        }

        public void decrement() {
            this.number--;
        }
    }
}
