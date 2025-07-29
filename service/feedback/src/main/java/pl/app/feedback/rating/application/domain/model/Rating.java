package pl.app.feedback.rating.application.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rating")
@Getter
@NoArgsConstructor
public class Rating {
    @Id
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;

    private String userId;
    private Double rating;

    public Rating(String domainObjectType, String domainObjectId, String userId, Double rating) {
        this.id = ObjectId.get();
        this.domainObjectType = domainObjectType;
        this.domainObjectId = domainObjectId;
        this.userId = userId;
        this.rating = rating;
    }

    public Double setNewRating(Double newRating) {
        var oldRating = this.rating;
        this.rating = newRating;
        return oldRating;
    }
}
