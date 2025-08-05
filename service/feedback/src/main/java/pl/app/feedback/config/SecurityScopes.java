package pl.app.feedback.config;

import lombok.Getter;

@Getter
public enum SecurityScopes {
    RATING_READ("fs.rating:read"),
    RATING_WRITE("fs.rating:write"),
    RATING_MANAGE("fs.rating:manage"),

    REACTION_READ("fs.reaction:read"),
    REACTION_WRITE("fs.reaction:write"),
    REACTION_MANAGE("fs.reaction:manage"),

    COMMENT_READ("fs.comment:read"),
    COMMENT_WRITE("fs.comment:write"),
    COMMENT_MODERATE("fs.comment:moderate"),
    COMMENT_MANAGE("fs.comment:manage"),

    COMMENT_REPORT_READ("fs.comment.report:read"),
    COMMENT_REPORT_WRITE("fs.comment.report:write"),
    COMMENT_REPORT_MANAGE("fs.comment.report:manage");

    private final String scopeName;

    SecurityScopes(String scopeName) {
        this.scopeName = scopeName;
    }
}
