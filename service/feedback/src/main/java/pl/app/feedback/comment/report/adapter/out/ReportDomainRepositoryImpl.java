package pl.app.feedback.comment.report.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.feedback.comment.report.application.domain.model.Report;
import pl.app.feedback.comment.report.application.domain.model.ReportException;
import pl.app.feedback.comment.report.application.port.out.ReportDomainRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class ReportDomainRepositoryImpl implements ReportDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Report> fetch(ObjectId reportId) {
        return mongoTemplate.query(Report.class)
                .matching(Query.query(Criteria
                        .where("_id").is(reportId)
                )).one()
                .switchIfEmpty(Mono.error(ReportException.NotFoundReportException::new));
    }
}
