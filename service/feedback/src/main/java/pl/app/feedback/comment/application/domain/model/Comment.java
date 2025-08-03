package pl.app.feedback.comment.application.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comment")
@Getter
@NoArgsConstructor
public class Comment {
    @Id
    private ObjectId id;
    private String domainObjectType;
    private String domainObjectId;

    private String userId;
    private String content;
    private ObjectId parentId;
    private Status status;

    @JsonIgnore
    private Boolean isDeleted;

    @Version
    @JsonIgnore
    private Long version;

    public Comment(String domainObjectType, String domainObjectId, String userId, String content, ObjectId parentId) {
        this.id = ObjectId.get();
        this.domainObjectType = domainObjectType;
        this.domainObjectId = domainObjectId;
        this.userId = userId;
        this.content = content;
        this.parentId = parentId;
        this.status = Status.PUBLISHED;
        this.isDeleted = false;
        this.version = null;
    }

    public void updateContent(String newContent) {
        verifyThatCommentIsNotRemoved();
        this.content = newContent;
    }

    public void hide() {
        verifyThatCommentIsNotRemoved();
        if (this.status != Status.PUBLISHED) {
            throw new CommentException.InvalidStateReportException();
        }
        this.status = Status.HIDDEN;
    }

    public void restore() {
        verifyThatCommentIsNotRemoved();
        if (this.status != Status.HIDDEN) {
            throw new CommentException.InvalidStateReportException();
        }
        this.status = Status.PUBLISHED;
    }

    public void verifyThatCommentIsNotRemoved() {
        if (isDeleted) {
            throw new CommentException.InvalidStateReportException();
        }
    }

    public void markObjectAsRemoved() {
        verifyThatCommentIsNotRemoved();
        this.isDeleted = true;
    }

    public enum Status {
        PUBLISHED,
        HIDDEN,
    }
}
