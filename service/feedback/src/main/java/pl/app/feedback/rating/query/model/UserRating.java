package pl.app.feedback.rating.query.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.feedback.rating.application.domain.model.RatingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Document(collection = "user-rating")
@Data
@NoArgsConstructor
public class UserRating {
    @Id
    private ObjectId id;
    private String userId;
    private List<Rating> ratings;
    @Version
    private Long version;

    public UserRating(String userId) {
        this.id = ObjectId.get();
        this.userId = userId;
        this.ratings = new ArrayList<>();
        this.version = null;
    }

    public void handle(RatingEvent.RatingCreatedEvent event) {
        getRating(event.id()).ifPresentOrElse(
                rating -> {
                    rating.setRating(event.rating());
                },
                () -> {
                    var rating = new Rating(event.id(), event.domainObjectType(), event.domainObjectId(), event.userId(), event.rating());
                    ratings.add(rating);
                }
        );
    }

    public void handle(RatingEvent.RatingUpdatedEvent event) {
        getRating(event.id()).ifPresentOrElse(
                rating -> {
                    rating.setRating(event.rating());
                },
                () -> {
                    var rating = new Rating(event.id(), event.domainObjectType(), event.domainObjectId(), event.userId(), event.rating());
                    ratings.add(rating);
                }
        );
    }

    public void handle(RatingEvent.RatingRemovedEvent event) {
        getRating(event.id()).ifPresentOrElse(
                rating -> {
                    ratings.remove(rating);
                },
                () -> {
                }
        );
    }

    private Optional<Rating> getRating(ObjectId ratingId) {
        return ratings.stream()
                .filter(r -> r.getId().equals(ratingId))
                .findAny();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static public class Rating {
        private ObjectId id;
        private String domainObjectType;
        private String domainObjectId;
        private String userId;
        private Double rating;
    }
}
