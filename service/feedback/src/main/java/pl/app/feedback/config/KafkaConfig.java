package pl.app.feedback.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.BackOff;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableKafka
@PropertySource("classpath:kafka.properties")
public class KafkaConfig {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);
    private final KafkaTopicProperties topicNames;
    @Value("${app.kafka.bootstrap.servers}")
    private String bootstrapServers;

    public KafkaConfig(KafkaTopicProperties topicNames) {
        this.topicNames = topicNames;
    }

    @Configuration
    class TopicConfiguration {
        @Bean
        KafkaAdmin admin() {
            Map<String, Object> configs = new HashMap<>();
            configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            return new KafkaAdmin(configs);
        }

        @Bean
        KafkaAdmin.NewTopics createTopics() {
            NewTopic[] array = topicNames.getAllTopics().stream()
                    .map(this::createTopicFromConfig)
                    .flatMap(List::stream)
                    .toArray(NewTopic[]::new);
            return new KafkaAdmin.NewTopics(array);
        }

        private List<NewTopic> createTopicFromConfig(KafkaTopicProperties.Topic topic) {
            return topic.getTopicNames().stream()
                    .map(name -> TopicBuilder.name(name).partitions(topic.getPartitions()).compact().build())
                    .collect(Collectors.toList());
        }
    }

    @Configuration
    class ConsumerConfiguration {

        @Value("${app.kafka.consumer.client-id}")
        private String consumerClientId;

        @Bean
        public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<ObjectId, Object>> kafkaListenerContainerFactory(
                CommonErrorHandler commonErrorHandler,
                Deserializer<ObjectId> objectIdDeserializer,
                JsonDeserializer<Object> jsonDeserializer
        ) {
            ConsumerFactory<ObjectId, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerConfig(),
                    () -> objectIdDeserializer, () -> jsonDeserializer);
            ConcurrentKafkaListenerContainerFactory<ObjectId, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory);
            factory.setConcurrency(1);
            factory.getContainerProperties().setPollTimeout(3000);
            factory.setCommonErrorHandler(commonErrorHandler);
            return factory;
        }

        @Bean
        CommonErrorHandler commonErrorHandler(
                BackOff defaultBackOff,
                ConsumerRecordRecoverer deadLetterRecoverer
        ) {
            CommonErrorHandler defaultErrorHandler = new DefaultErrorHandler(deadLetterRecoverer, defaultBackOff);
//            defaultErrorHandler.addNotRetryableExceptions();
            return defaultErrorHandler;
        }

        @Bean
        ConsumerRecordRecoverer deadLetterRecoverer(
                KafkaTemplate<ObjectId, Object> objectIdTemplate,
                KafkaTopicProperties properties
        ) {
            Map<Class<?>, KafkaOperations<?, ?>> templates = new LinkedHashMap<>() {{
                put(Object.class, objectIdTemplate);
            }};

            ConsumerRecordRecoverer deadLetterRecoverer = new DeadLetterPublishingRecoverer(templates, (consumerRecord, ex) -> {
                var destinationTopicName = properties.getTopic(consumerRecord.value()).getDtlTopicName();
                logger.error("send unprocessed event to: {}, event: {}, because of exception: {}", destinationTopicName, consumerRecord.value(), ex.getCause().getMessage());
                return new TopicPartition(destinationTopicName, -1);  //If the returned TopicPartition has a negative partition, the partition is not set in the ProducerRecord, so the partition is selected by Kafka
            });
            return deadLetterRecoverer;
        }

        @Bean
        BackOff defaultBackOff() {
            ExponentialBackOffWithMaxRetries defaultBackOff = new ExponentialBackOffWithMaxRetries(6);
            defaultBackOff.setInitialInterval(1_000L);
            defaultBackOff.setMultiplier(2.0);
            defaultBackOff.setMaxInterval(10_000L);
            return defaultBackOff;
        }

        @Bean
        JsonDeserializer<Object> jsonDeserializer(ObjectMapper objectMapper) {
            JsonDeserializer<Object> deserializer = new JsonDeserializer<>(objectMapper);
            deserializer.setRemoveTypeHeaders(false);
            deserializer.addTrustedPackages("*");
            return deserializer;
        }

        @Bean
        Deserializer<ObjectId> objectIdDeserializer() {
            return new Deserializer<ObjectId>() {
                @Override
                public ObjectId deserialize(String topic, byte[] data) {
                    if (Objects.isNull(data) || data.length == 0) {
                        return null;
                    }
                    return new ObjectId(data);
                }
            };
        }

        @Bean
        Map<String, Object> consumerConfig() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            props.put(ConsumerConfig.CLIENT_ID_CONFIG, consumerClientId);
            return props;
        }

    }

    @Configuration
    @RequiredArgsConstructor
    class ProducerConfiguration {
        private final KafkaAdmin kafkaAdmin;
        @Value("${app.kafka.producer.client-id}")
        private String producerClientId;

        @Bean
        KafkaTemplate<ObjectId, Object> objectIdTemplate(
                Serializer<ObjectId> objectIdSerializer,
                JsonSerializer<Object> jsonSerializer
        ) {
            var kafkaFactory = new DefaultKafkaProducerFactory<>(producerConfigs(), () -> objectIdSerializer, () -> jsonSerializer);
            var kafkaTemplate = new KafkaTemplate<>(kafkaFactory);
            kafkaTemplate.setProducerInterceptor(producerInterceptor());
            kafkaTemplate.setKafkaAdmin(kafkaAdmin);
            refreshTopicPublisher(kafkaTemplate).subscribe();
            return kafkaTemplate;
        }

        // method that refresh metadata, bc of a bug
        private Flux<Long> refreshTopicPublisher(KafkaTemplate<ObjectId, Object> kafkaTemplate) {
            logger.debug("Refreshing topic metadata");
            topicNames.getAllTopicNames().forEach(kafkaTemplate::partitionsFor);
            return Flux.interval(Duration.ofMinutes(9))
                    .doOnNext(tick -> {
                        logger.debug("Refreshing topic metadata");
                        topicNames.getAllTopicNames().forEach(kafkaTemplate::partitionsFor);
                    });
        }

        ProducerInterceptor<ObjectId, Object> producerInterceptor() {
            return new ProducerInterceptor<ObjectId, Object>() {
                @Override
                public ProducerRecord<ObjectId, Object> onSend(ProducerRecord<ObjectId, Object> producerRecord) {
                    logger.debug("send: {}", producerRecord.value());
                    return producerRecord;
                }

                @Override
                public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {

                }

                @Override
                public void close() {

                }

                @Override
                public void configure(Map<String, ?> map) {

                }
            };
        }

        @Bean
        JsonSerializer<Object> jsonSerializer(ObjectMapper objectMapper) {
            return new JsonSerializer<>(objectMapper);
        }

        @Bean
        Serializer<ObjectId> objectIdSerializer() {
            return new Serializer<ObjectId>() {
                @Override
                public byte[] serialize(String topic, ObjectId data) {
                    if (Objects.isNull(data)) {
                        return new byte[0];
                    }
                    return data.toByteArray();
                }
            };
        }

        @Bean
        Map<String, Object> producerConfigs() {
            Map<String, Object> props = new HashMap<>();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            props.put(ProducerConfig.CLIENT_ID_CONFIG, producerClientId);
            props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 300_000);
            props.put(ProducerConfig.METADATA_MAX_IDLE_CONFIG, 600_000);
            props.put(ProducerConfig.METADATA_MAX_AGE_CONFIG, 600_000);
            return props;
        }
    }
}
