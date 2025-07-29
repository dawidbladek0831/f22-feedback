package pl.app.feedback.rating.query.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "domain-object-rating")
@Data
@NoArgsConstructor
public class DomainObjectRating {
    @Id
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;
    private Double rating;
    private Double sum;
    private Double quantity;
    @Version
    private Long version;

    public DomainObjectRating(String domainObjectType, String domainObjectId) {
        this.id = ObjectId.get();
        this.domainObjectType = domainObjectType;
        this.domainObjectId = domainObjectId;
        this.rating = 0d;
        this.sum = 0d;
        this.quantity = 0d;
    }

    public void calculateRating() {
        if (quantity > 0) {
            rating = sum / quantity;
        } else {
            rating = 0d;
        }
    }
}
