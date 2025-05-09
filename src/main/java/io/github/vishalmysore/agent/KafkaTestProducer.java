package io.github.vishalmysore.agent;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Test utility to produce messages to the embedded Kafka broker
 */
@Slf4j
@Component
public class KafkaTestProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaTestProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderMessage(String orderId, String orderData) {
        log.info("Sending order message: {}", orderData);
        kafkaTemplate.send("orders", orderId, orderData);
    }

    public void sendPaymentMessage(String paymentId, String paymentData) {
        log.info("Sending payment message: {}", paymentData);
        kafkaTemplate.send("payments", paymentId, paymentData);
    }

    public void sendAlertMessage(String alertId, String alertData) {
        log.info("Sending alert message: {}", alertData);
        kafkaTemplate.send("alerts", alertId, alertData);
    }
}