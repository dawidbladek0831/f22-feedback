package pl.app.feedback.reaction.query.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
