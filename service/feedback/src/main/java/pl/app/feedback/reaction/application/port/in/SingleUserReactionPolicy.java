package pl.app.feedback.reaction.application.port.in;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import pl.app.common.event.EventPublisher;
import pl.app.feedback.reaction.application.domain.ReactionEvent;
import pl.app.feedback.reaction.application.domain.UserReaction;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@Component
class SingleUserReactionPolicy {
    private static final Logger logger = LoggerFactory.getLogger(SingleUserReactionPolicy.class);
    private final SingleUserReactionPolicyProperties properties;
    private final EventPublisher eventPublisher;

    public SingleUserReactionPolicy(SingleUserReactionPolicyProperties properties, EventPublisher eventPublisher) {
        this.properties = properties;
        this.eventPublisher = eventPublisher;
        if (!isPolicyEnable()) {
            logger.info("policy is DISABLE");
        } else {
            logger.info("policy is ENABLE. Default type is: {}. Adjusted types: {}", properties.getDefaultEnable(), properties.getTypes());
        }
    }

    public Mono<UserReaction> apply(UserReaction domain) {
        if (!isPolicyEnable()) {
            return Mono.just(domain);
        }
        if (isTypeConfigured(domain.getDomainObjectType())) {
            if (properties.getTypes().get(domain.getDomainObjectType()).getEnable()) {
                var removedReactions = domain.removeReactionAll();
                var events = removedReactions.stream().map(reaction -> new ReactionEvent.UserReactionRemovedEvent(
                        domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId(), domain.getUserId(), reaction
                )).collect(Collectors.toSet());
                return eventPublisher.publishCollection(events).then(Mono.just(domain));
            }
            return Mono.just(domain);
        }
        if (properties.getDefaultEnable()) {
            var removedReactions = domain.removeReactionAll();
            var events = removedReactions.stream().map(reaction -> new ReactionEvent.UserReactionRemovedEvent(
                    domain.getId(), domain.getDomainObjectType(), domain.getDomainObjectId(), domain.getUserId(), reaction
            )).collect(Collectors.toSet());
            return eventPublisher.publishCollection(events).then(Mono.just(domain));
        }
        return Mono.just(domain);
    }

    private boolean isTypeConfigured(String domainObjectType) {
        return properties.getTypes().containsKey(domainObjectType);
    }

    private boolean isPolicyEnable() {
        return properties.getEnable();
    }

    @Data
    @Component
    @ConfigurationProperties(prefix = "app.reaction.policy.single-user-reaction-policy")
    static class SingleUserReactionPolicyProperties {
        private Boolean enable = false;
        private Boolean defaultEnable = true;
        private Map<String, Type> types = Map.of();

        @Data
        public static class Type {
            private Boolean enable = false;
        }
    }
}
