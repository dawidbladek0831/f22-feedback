package pl.app.feedback.rating.query.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.feedback.rating.application.domain.model.RatingEvent;

@Document(collection = "domain-object-rating")
@Data
@NoArgsConstructor
public class DomainObjectRating {
    @Id
    @JsonIgnore
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;
    private Double rating;
    private Double sum;
    private Double quantity;
    @Version
    @JsonIgnore
    private Long version;

    public DomainObjectRating(String domainObjectType, String domainObjectId) {
        this.id = ObjectId.get();
        this.domainObjectType = domainObjectType;
        this.domainObjectId = domainObjectId;
        this.rating = 0d;
        this.sum = 0d;
        this.quantity = 0d;
    }

    public void handle(RatingEvent.RatingCreatedEvent event) {
        sum += event.rating();
        quantity += 1;
        calculateRating();
    }

    public void handle(RatingEvent.RatingUpdatedEvent event) {
        sum += event.rating() - event.oldRating();
        calculateRating();
    }

    public void handle(RatingEvent.RatingRemovedEvent event) {
        sum -= event.rating();
        quantity -= 1;
        calculateRating();
    }

    public void calculateRating() {
        if (quantity > 0) {
            rating = sum / quantity;
        } else {
            rating = 0d;
        }
    }
}
