package pl.app.feedback.comment.report.application.port.in;

import pl.app.feedback.comment.report.application.domain.model.Report;
import reactor.core.publisher.Mono;

public interface ReportService {
    Mono<Report> create(ReportCommand.CreateReportCommand command);

    Mono<Report> approve(ReportCommand.ApproveReportCommand command);

    Mono<Report> reject(ReportCommand.RejectReportCommand command);
}