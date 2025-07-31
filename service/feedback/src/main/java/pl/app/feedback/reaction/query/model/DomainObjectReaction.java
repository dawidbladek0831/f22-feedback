package pl.app.feedback.reaction.query.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.feedback.reaction.application.domain.model.ReactionEvent;

import java.util.HashMap;
import java.util.Map;

@Document(collection = "domain-object-reaction")
@Data
@NoArgsConstructor
public class DomainObjectReaction {
    @Id
    @JsonIgnore
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;
    private Map<String, Integer> reactions;
    @Version
    @JsonIgnore
    private Long version;

    public DomainObjectReaction(String domainObjectType, String domainObjectId) {
        this.id = ObjectId.get();
        this.domainObjectType = domainObjectType;
        this.domainObjectId = domainObjectId;
        this.reactions = new HashMap<>();
    }

    public void handle(ReactionEvent.ReactionAddedEvent event) {
        reactions.merge(event.reaction(), 1, (oldValue, value) -> oldValue + 1);
    }

    public void handle(ReactionEvent.ReactionRemovedEvent event) {
        reactions.merge(event.reaction(), 1, (oldValue, value) -> oldValue - 1);
    }

}
