package pl.app.feedback.reaction.application.port.in;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import pl.app.feedback.reaction.application.domain.ReactionException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
class AllowedDomainObjectTypesPolicy {
    private static final Logger logger = LoggerFactory.getLogger(AllowedDomainObjectTypesPolicy.class);
    private final AllowedDomainObjectTypesPolicyProperties properties;

    public AllowedDomainObjectTypesPolicy(AllowedDomainObjectTypesPolicyProperties properties) {
        this.properties = properties;
        if (!isPolicyEnable()) {
            logger.info("policy is DISABLE");
        } else {
            logger.info("policy is ENABLE. Allowed domain object types: {}", properties.getTypes());
        }
    }

    public Mono<Void> apply(String domainObjectType) {
        if (!isPolicyEnable()) {
            return Mono.empty();
        }
        if (properties.getTypes().contains(domainObjectType)) {
            return Mono.empty();
        }
        throw ReactionException.InvalidDomainObjectTypeException.domainObjectType(domainObjectType);
    }

    private boolean isPolicyEnable() {
        return properties.getEnable() && !properties.getTypes().isEmpty();
    }

    @Data
    @Component
    @ConfigurationProperties(prefix = "app.reaction.policy.allowed-domain-object-types-policy")
    static class AllowedDomainObjectTypesPolicyProperties {
        private Boolean enable = false;
        private List<String> types = List.of();

        public void setTypes(List<String> types) {
            this.types = types.stream()
                    .map(String::toUpperCase)
                    .toList();
        }
    }
}


