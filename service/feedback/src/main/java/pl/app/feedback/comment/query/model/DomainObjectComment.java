package pl.app.feedback.comment.query.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.feedback.comment.application.domain.model.CommentEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


@Document(collection = "domain-object-comment")
@Data
@NoArgsConstructor
@CompoundIndex(name = "domain-object_idx", def = "{'domainObjectType': 1, 'domainObjectId': 1}")
public class DomainObjectComment {
    @Id
    @JsonIgnore
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;
    private List<Comment> comments;
    @Version
    @JsonIgnore
    private Long version;

    public DomainObjectComment(String domainObjectType, String domainObjectId) {
        this.id = ObjectId.get();
        this.domainObjectType = domainObjectType;
        this.domainObjectId = domainObjectId;
        this.comments = new LinkedList<>();
    }

    public void handle(CommentEvent.CommentCreatedEvent event) {
        var comment = new Comment(
                event.id(),
                event.domainObjectType(),
                event.domainObjectId(),
                event.userId(),
                event.content(),
                event.parentId(),
                pl.app.feedback.comment.application.domain.model.Comment.Status.PUBLISHED,
                false);
        this.comments.add(comment);
    }

    public void handle(CommentEvent.CommentUpdatedEvent event) {
        var comment = getComment(event.id()).orElseThrow();
        comment.setContent(event.content());
    }

    public void handle(CommentEvent.CommentRemovedEvent event) {
        var comment = getComment(event.id()).orElseThrow();
        comment.setIsDeleted(true);
    }

    public void handle(CommentEvent.CommentHiddenEvent event) {
        var comment = getComment(event.id()).orElseThrow();
        comment.setStatus(pl.app.feedback.comment.application.domain.model.Comment.Status.HIDDEN);
    }

    public void handle(CommentEvent.CommentRestoredEvent event) {
        var comment = getComment(event.id()).orElseThrow();
        comment.setStatus(pl.app.feedback.comment.application.domain.model.Comment.Status.PUBLISHED);
    }

    private Optional<Comment> getComment(ObjectId id) {
        return this.comments.stream().filter(e -> e.getId().equals(id)).findAny();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comment {
        private ObjectId id;
        private String domainObjectType;
        private String domainObjectId;

        private String userId;
        private String content;
        private ObjectId parentId;
        private pl.app.feedback.comment.application.domain.model.Comment.Status status;
        private Boolean isDeleted;
    }

}
