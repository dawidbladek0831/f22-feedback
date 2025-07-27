package pl.app.feedback.reaction.application.domain.service;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import pl.app.feedback.reaction.application.domain.model.ReactionException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
class AllowedReactionsPolicy {
    private static final Logger logger = LoggerFactory.getLogger(AllowedReactionsPolicy.class);
    private final AllowedReactionsPolicyProperties properties;

    public AllowedReactionsPolicy(AllowedReactionsPolicyProperties properties) {
        this.properties = properties;
        if (!isPolicyEnable()) {
            logger.info("policy is DISABLE");
        } else {
            logger.info("policy is ENABLE. Allowed default reactions: {}. Adjusted domain object types with reactions: {}", properties.getDefaultReactions(), properties.getTypes());
        }
    }

    public Mono<Void> apply(String domainObjectType, String reaction) {
        if (!isPolicyEnable()) {
            return Mono.empty();
        }
        if(isTypeReactionsConfigured(domainObjectType)){
            if(properties.getTypes().get(domainObjectType).getReactions().contains(reaction)){
                return Mono.empty();
            }
            throw ReactionException.InvalidReactionException.reaction(reaction);
        }
        if(isDefaultReactionsConfigured()){
            if(properties.getDefaultReactions().contains(reaction)){
                return Mono.empty();
            }
            throw ReactionException.InvalidReactionException.reaction(reaction);
        }
        return Mono.empty();
    }

    private boolean isDefaultReactionsConfigured() {
        return !properties.getDefaultReactions().isEmpty();
    }

    private boolean isTypeReactionsConfigured(String domainObjectType) {
        return properties.getTypes().containsKey(domainObjectType);
    }

    private boolean isPolicyEnable() {
        return properties.getEnable();
    }

    @Data
    @Component
    @ConfigurationProperties(prefix = "app.reaction.policy.allowed-reactions-policy")
    static class AllowedReactionsPolicyProperties {
        private Boolean enable = false;
        private List<String> defaultReactions = List.of();
        private Map<String, Type> types = Map.of();

        @Data
        public static class Type {
            private List<String> reactions = List.of();

            public void normalize() {
                this.reactions = this.reactions.stream()
                        .map(String::toUpperCase)
                        .toList();
            }
        }

        public void setDefaultReactions(List<String> defaultReactions) {
            this.defaultReactions = defaultReactions.stream()
                    .map(String::toUpperCase)
                    .toList();
        }

        public void setTypes(Map<String, Type> types) {
            this.types = types.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().toUpperCase(),
                            e -> {
                                e.getValue().normalize();
                                return e.getValue();
                            }
                    ));
        }
    }
}
