package pl.app.feedback.reaction.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReactionDto {
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;
    private String userId;
    private Set<String> reactions;
}
