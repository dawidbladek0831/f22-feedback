package pl.app.feedback.rating.query.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.feedback.rating.application.domain.model.Rating;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
