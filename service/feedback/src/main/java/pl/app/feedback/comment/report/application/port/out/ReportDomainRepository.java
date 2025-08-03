package pl.app.feedback.comment.report.application.port.out;

import org.bson.types.ObjectId;
import pl.app.feedback.comment.report.application.domain.model.Report;
import reactor.core.publisher.Mono;

public interface ReportDomainRepository {
    Mono<Report> fetch(ObjectId reportId);
}
