package pl.app.feedback.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@ConfigurationProperties(prefix = "app.kafka.topic")
@PropertySource("classpath:kafka.properties")
public class KafkaTopicProperties {
    private Topic reactionCreated;
    private Topic reactionAdded;
    private Topic reactionRemoved;

    public List<Topic> getAllTopics() {
        return List.of(
                reactionCreated,
                reactionAdded,
                reactionRemoved
        );
    }

    public List<String> getAllTopicNames() {
        return getAllTopics().stream()
                .map(Topic::getTopicNames)
                .flatMap(List::stream)
                .toList();
    }

    @Setter
    @Getter
    public static class Topic {
        private String name;
        private Integer partitions;
        private Boolean dtlTopic;

        public Topic() {
            this.name = "NAME_NOT_CONFIGURED";
            this.partitions = 1;
            this.dtlTopic = true;
        }

        public List<String> getTopicNames() {
            return dtlTopic ? List.of(name, name + ".DTL") : List.of(name);
        }
    }
}
