package pl.app.feedback.comment.report.query.adapter;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.app.feedback.comment.report.application.domain.model.Report;
import pl.app.feedback.comment.report.query.port.ReportQueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ReportQueryRestController.resourcePath)
@RequiredArgsConstructor
class ReportQueryRestController {
    public static final String resourceName = "reports";
    public static final String resourcePath = "/api/v1/" + resourceName;
    private final ReportQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Flux<Report>>> fetchAllBy(
            @RequestParam(required = false) ObjectId commentId,
            @RequestParam(required = false) Report.Status status,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer pageSize
    ) {
        return Mono.just(ResponseEntity.ok(queryService.fetchAllBy(commentId, status, cursor, pageSize)));
    }

}
