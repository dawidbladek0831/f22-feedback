package pl.app.feedback.rating.application.domain.service;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import pl.app.feedback.rating.application.domain.model.RatingException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@Component
class RangeRatingPolicy {
    private static final Logger logger = LoggerFactory.getLogger(RangeRatingPolicy.class);
    private final RangeRatingPolicyProperties properties;

    public RangeRatingPolicy(RangeRatingPolicyProperties properties) {
        this.properties = properties;
        if (!isPolicyEnable()) {
            logger.info("policy is DISABLE");
        } else {
            logger.info("policy is ENABLE. Default range: {}-{}. Adjusted domain object types: {}", properties.getDefaultMin(), properties.getDefaultMax(), properties.getTypes());
        }
    }

    public Mono<Void> apply(String domainObjectType, Double rating) {
        if (!isPolicyEnable()) {
            return Mono.empty();
        }
        if (isTypeReactionsConfigured(domainObjectType)) {
            var min = properties.getTypes().get(domainObjectType).min;
            var max = properties.getTypes().get(domainObjectType).max;
            if (min <= rating && max >= rating) {
                return Mono.empty();
            }
            throw RatingException.InvalidRatingException.range(min, max);
        }

        var min = properties.defaultMin;
        var max = properties.defaultMax;
        if (min <= rating && max >= rating) {
            return Mono.empty();
        }
        throw RatingException.InvalidRatingException.range(min, max);
    }

    private boolean isTypeReactionsConfigured(String domainObjectType) {
        return properties.getTypes().containsKey(domainObjectType);
    }

    private boolean isPolicyEnable() {
        return properties.getEnable();
    }

    @Data
    @Component
    @ConfigurationProperties(prefix = "app.reaction.policy.range-rating-policy")
    static class RangeRatingPolicyProperties {
        private Boolean enable = false;
        private Double defaultMin = 0d;
        private Double defaultMax = 10d;
        private Map<String, Type> types = Map.of();

        public void setTypes(Map<String, Type> types) {
            this.types = types.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().toUpperCase(),
                            Map.Entry::getValue
                    ));
        }

        @Data
        public static class Type {
            private Double min = 0d;
            private Double max = 10d;
        }
    }
}
