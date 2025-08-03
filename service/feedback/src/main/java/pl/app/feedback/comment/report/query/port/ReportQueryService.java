package pl.app.feedback.comment.report.query.port;

import org.bson.types.ObjectId;
import pl.app.feedback.comment.report.application.domain.model.Report;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReportQueryService {
    Flux<Report> fetchAll();

    Mono<Report> fetchBy(ObjectId reportId);

    Flux<Report> fetchAllBy(ObjectId commentId, Report.Status status, String cursor, Integer pageSize);
}
