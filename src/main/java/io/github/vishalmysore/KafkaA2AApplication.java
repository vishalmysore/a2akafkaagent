package io.github.vishalmysore;
import io.github.vishalmysore.agent.KafkaTestProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"io.github.vishalmysore.a2a", "io.github.vishalmysore.agent", "io.github.vishalmysore.a2a.kafka"})
public class KafkaA2AApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaA2AApplication.class, args);
    }

    @Bean
    public CommandLineRunner demoData(KafkaTestProducer producer) {
        return args -> {
            // Produce some sample messages for demonstration
            producer.sendOrderMessage("ORD-1", "{\"id\":\"ORD-1\", \"status\":\"created\", \"amount\":150.00}");
            producer.sendPaymentMessage("PAY-1", "{\"id\":\"PAY-1\", \"status\":\"authorized\", \"amount\":150.00}");
            producer.sendAlertMessage("ALT-1", "{\"type\":\"CPU_USAGE\", \"severity\":\"HIGH\", \"value\":95.2}");
        };
    }
}