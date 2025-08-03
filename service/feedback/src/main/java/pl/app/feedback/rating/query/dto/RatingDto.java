package pl.app.feedback.rating.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingDto {
    private String domainObjectType;
    private String domainObjectId;
    private String userId;
    private Double rating;
}
