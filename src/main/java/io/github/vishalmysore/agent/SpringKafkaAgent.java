package io.github.vishalmysore.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vishalmysore.a2a.client.A2ATaskClient;
import io.github.vishalmysore.a2a.client.LocalA2ATaskClient;
import io.github.vishalmysore.a2a.domain.Task;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Enhanced KafkaAgent using Spring Kafka's @KafkaListener
 */
@Slf4j
@Component
public class SpringKafkaAgent {

    private final LocalA2ATaskClient client;
    private final ObjectMapper objectMapper;

    public SpringKafkaAgent(LocalA2ATaskClient client) {
        this.client = client;
        this.objectMapper = new ObjectMapper();
        log.info("SpringKafkaAgent initialized with A2A client");
    }    @KafkaListener(topics = "orders", groupId = "a2a-group")
    public void consumeOrderMessages(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            log.info("Received order message: key={}, value={}", record.key(), record.value());
            processMessage("order-processing", record.topic(), record.key(), record.value());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing order message", e);
        }
    }

    @KafkaListener(topics = "payments", groupId = "a2a-group")
    public void consumePaymentMessages(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            log.info("Received payment message: key={}, value={}", record.key(), record.value());
            processMessage("payment-processing", record.topic(), record.key(), record.value());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing payment message", e);
        }
    }

    @KafkaListener(topics = "alerts", groupId = "a2a-group")
    public void consumeAlertMessages(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            log.info("Received alert message: key={}, value={}", record.key(), record.value());
            processMessage("system-alert", record.topic(), record.key(), record.value());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing alert message", e);
        }
    }

    private void processMessage(String messageType, String topic, String key, String value) {
        try {
            // Create a task with the message content
            String taskDescription = String.format("kafka-message:%s topic:%s key:%s value:%s",
                    messageType, topic, key, value);

            Task task = client.sendTask(taskDescription);
            log.info("Created task {} for Kafka message from topic {}", task.getId(), topic);

            // Optionally wait for task result
            Task result = client.getTask(task.getId(), 5);
            log.info("Task result: {}", result);
        } catch (Exception e) {
            log.error("Failed to create task for Kafka message", e);
        }
    }
}
