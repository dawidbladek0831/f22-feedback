package pl.app.feedback.reaction.query.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document(collection = "domain-object-reaction")
@Data
@NoArgsConstructor
public class DomainObjectReaction {
    @Id
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;
    private Map<String, Integer> reactions;

    public DomainObjectReaction(String domainObjectType, String domainObjectId) {
        this.id = ObjectId.get();
        this.domainObjectType = domainObjectType;
        this.domainObjectId = domainObjectId;
        this.reactions = new HashMap<>();
    }
}
