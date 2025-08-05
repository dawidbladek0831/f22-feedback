package pl.app.feedback.reaction.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DomainObjectReactionDto {
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;
    private List<Reaction> reactions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Reaction {
        private String name;
        private Integer quantity;
    }
}
