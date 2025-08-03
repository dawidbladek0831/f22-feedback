package pl.app.feedback.comment.report.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.feedback.comment.report.application.domain.model.Report;
import pl.app.feedback.comment.report.application.port.in.ReportCommand;
import pl.app.feedback.comment.report.application.port.in.ReportService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ReportRestController.resourcePath)
@RequiredArgsConstructor
class ReportRestController {
    public static final String resourceName = "reports";
    public static final String resourcePath = "/api/v1/comments/{commentId}/" + resourceName;
    private final ReportService service;

    @PostMapping
    Mono<ResponseEntity<Report>> create(
            @PathVariable ObjectId commentId,
            @RequestBody ReportCommand.CreateReportCommand command
    ) {
        return service.create(command)
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }

    @PostMapping("/{reportId}/approvals")
    Mono<ResponseEntity<Report>> approve(
            @PathVariable ObjectId commentId,
            @PathVariable ObjectId reportId
    ) {
        return service.approve(new ReportCommand.ApproveReportCommand(reportId))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }

    @PostMapping("/{reportId}/rejections")
    Mono<ResponseEntity<Report>> reject(
            @PathVariable ObjectId commentId,
            @PathVariable ObjectId reportId
    ) {
        return service.reject(new ReportCommand.RejectReportCommand(reportId))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }
}
