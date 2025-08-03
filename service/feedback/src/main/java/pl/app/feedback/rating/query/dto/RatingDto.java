package pl.app.feedback.rating.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingDto {
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;
    private String userId;
    private Double rating;
}
