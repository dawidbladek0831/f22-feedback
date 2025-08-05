package pl.app.feedback.comment.report.application.domain.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import pl.app.common.event.EventPublisher;
import pl.app.feedback.comment.application.domain.model.Comment;
import pl.app.feedback.comment.application.domain.model.CommentException;
import pl.app.feedback.comment.application.port.out.CommentDomainRepository;
import pl.app.feedback.comment.report.application.domain.model.Report;
import pl.app.feedback.comment.report.application.domain.model.ReportEvent;
import pl.app.feedback.comment.report.application.port.in.ReportCommand;
import pl.app.feedback.comment.report.application.port.in.ReportService;
import pl.app.feedback.comment.report.application.port.out.ReportDomainRepository;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
class ReportServiceImpl implements ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final EventPublisher eventPublisher;
    private final ReportDomainRepository repository;

    private final CommentDomainRepository commentDomainRepository;

    @Override
    public Mono<Report> create(ReportCommand.CreateReportCommand command) {
        return Mono.fromCallable(() ->
                verifyCommentExists(command.commentId()).flatMap(comment -> {
                    var domain = new Report(command.commentId(), command.reason(), command.userId());
                    return mongoTemplate.insert(domain)
                            .then(eventPublisher.publish(new ReportEvent.ReportCreatedEvent(domain.getId(), domain.getCommentId(), domain.getReason(), domain.getUserId())))
                            .thenReturn(domain);
                })
        ).doOnSubscribe(subscription ->
                logger.debug("creating report to comment: {}", command.commentId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created report: {} to comment: {}", domain.getId(), command.commentId())
        ).doOnError(e ->
                logger.error("exception occurred while creating report: {}, exception: {}", command.commentId(), e.toString())
        );
    }

    private Mono<Comment> verifyCommentExists(ObjectId commentId) {
        return commentDomainRepository.fetch(commentId)
                .switchIfEmpty(Mono.error(CommentException.NotFoundCommentException::new));
    }

    @Override
    public Mono<Report> approve(ReportCommand.ApproveReportCommand command) {
        return Mono.fromCallable(() -> repository.fetch(command.reportId())
                .flatMap(domain -> {
                    domain.approve();
                    return mongoTemplate.save(domain)
                            .then(eventPublisher.publish(new ReportEvent.ReportApprovedEvent(domain.getId(), domain.getCommentId())))
                            .thenReturn(domain);
                })
        ).doOnSubscribe(subscription ->
                logger.debug("approving report: {}", command.reportId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("approved report: {}", command.reportId())
        ).doOnError(e ->
                logger.error("exception occurred while approving report: {}, exception: {}", command.reportId(), e.toString())
        );
    }

    @Override
    public Mono<Report> reject(ReportCommand.RejectReportCommand command) {
        return Mono.fromCallable(() -> repository.fetch(command.reportId())
                .flatMap(domain -> {
                    domain.reject();
                    return mongoTemplate.save(domain)
                            .then(eventPublisher.publish(new ReportEvent.ReportRejectedEvent(domain.getId(), domain.getCommentId())))
                            .thenReturn(domain);
                })
        ).doOnSubscribe(subscription ->
                logger.debug("rejecting report: {}", command.reportId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("rejected report: {}", command.reportId())
        ).doOnError(e ->
                logger.error("exception occurred while rejecting report: {}, exception: {}", command.reportId(), e.toString())
        );
    }
}
