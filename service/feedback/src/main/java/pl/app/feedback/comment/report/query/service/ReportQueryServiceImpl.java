package pl.app.feedback.comment.report.query.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.feedback.comment.report.application.domain.model.Report;
import pl.app.feedback.comment.report.application.domain.model.ReportException;
import pl.app.feedback.comment.report.query.port.ReportQueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RequiredArgsConstructor
class ReportQueryServiceImpl implements ReportQueryService {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<Report> fetchAll() {
        return mongoTemplate.query(Report.class).all();
    }

    @Override
    public Mono<Report> fetchBy(ObjectId reportId) {
        return mongoTemplate.query(Report.class)
                .matching(Query.query(Criteria
                        .where("_id").is(reportId)
                )).one()
                .switchIfEmpty(Mono.error(new ReportException.NotFoundReportException()));
    }

    @Override
    public Flux<Report> fetchAllBy(ObjectId commentId, Report.Status status, String cursor, Integer pageSize) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(commentId)) {
            criteria = criteria.and("commentId").is(commentId);
        }
        if (Objects.nonNull(status)) {
            criteria = criteria.and("status").is(status);
        }
        if (Objects.nonNull(cursor)) {
            if (ObjectId.isValid(cursor)) {
                criteria = criteria.and("_id").gt(new ObjectId(cursor));
            }
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 50;
        }
        Query query = Query.query(criteria).limit(pageSize)
                .with(Sort.by(Sort.Direction.ASC, "_id"));
        return mongoTemplate.query(Report.class)
                .matching(query)
                .all();
    }
}
