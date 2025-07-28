package pl.app.feedback.reaction.application.domain.service;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import pl.app.common.event.EventPublisher;
import pl.app.feedback.reaction.application.domain.model.Reaction;
import pl.app.feedback.reaction.application.domain.model.ReactionEvent;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
class LikeDislikePolicy {
    private static final Logger logger = LoggerFactory.getLogger(LikeDislikePolicy.class);
    private final LikeDislikePolicyProperties properties;
    private final EventPublisher eventPublisher;

    public LikeDislikePolicy(LikeDislikePolicyProperties properties, EventPublisher eventPublisher) {
        this.properties = properties;
        this.eventPublisher = eventPublisher;
        if (!isPolicyEnable()) {
            logger.info("policy is DISABLE");
        } else {
            logger.info("policy is ENABLE. Default type is: {}. Adjusted types: {}", properties.getDefaultEnable(), properties.getTypes());
        }
    }

    public Mono<Reaction> apply(Reaction domain, String newReaction) {
        if (!isPolicyEnable()) {
            return Mono.just(domain);
        }
        if (!(newReaction.equals("LIKE") || newReaction.equals("DISLIKE"))) {
            return Mono.just(domain);
        }
        if (isTypeConfigured(domain.getDomainObjectType())) {
            if (properties.getTypes().get(domain.getDomainObjectType()).getEnable()) {
                return removeOppositeReaction(domain, newReaction);
            }
            return Mono.just(domain);
        }
        if (properties.getDefaultEnable()) {
            return removeOppositeReaction(domain, newReaction);
        }
        return Mono.just(domain);
    }

    public Mono<Reaction> removeOppositeReaction(Reaction domain, String reaction) {
        if (reaction.equals("LIKE") && domain.containReaction("DISLIKE")) {
            return removeReaction(domain, "DISLIKE");
        }
        if (reaction.equals("DISLIKE") && domain.containReaction("LIKE")) {
            return removeReaction(domain, "LIKE");
        }
        return Mono.just(domain);
    }

    public Mono<Reaction> removeReaction(Reaction domain, String reaction) {
        domain.removeReaction(reaction);
        var event = new ReactionEvent.ReactionRemovedEvent(domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId(), domain.getUserId(), reaction);
        return eventPublisher.publish(event)
                .thenReturn(domain);
    }

    private boolean isTypeConfigured(String domainObjectType) {
        return properties.getTypes().containsKey(domainObjectType);
    }

    private boolean isPolicyEnable() {
        return properties.getEnable();
    }


    @Data
    @Component
    @ConfigurationProperties(prefix = "app.reaction.policy.like-dislike-policy")
    static class LikeDislikePolicyProperties {
        private Boolean enable = false;
        private Boolean defaultEnable = true;
        private Map<String, Type> types = Map.of();

        @Data
        public static class Type {
            private Boolean enable = false;
        }
    }
}
