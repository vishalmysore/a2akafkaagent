# Integrating Google's A2A Protocol with Apache Kafka

This project demonstrates the integration of Google's Agent-to-Agent (A2A) protocol with Apache Kafka using Spring Boot. The A2A protocol, developed by Google, enables seamless communication and task delegation between different AI agents. When combined with Kafka's robust messaging capabilities, it creates a powerful system for distributed task processing.

## What is A2AJava?
A2AJava is the Java implementation of the Google A2A protocol you can check the project [here](https://github.com/vishalmysore/a2ajav) .
it allows java based AI agents to  
- Discover each other's capabilities
- Delegate tasks between agents
- Track task progress and completion
- Handle complex workflows across distributed systems

## Project Overview

This implementation showcases how to:
1. Receive messages from Kafka topics
2. Convert these messages into A2A tasks
3. Process these tasks using specialized service agents
4. Track and manage task execution

The project implements three distinct services:
- **Order Service**: Handles order processing tasks
- **Payment Service**: Manages payment processing tasks
- **Alert Service**: Processes system alerts and notifications

Each service is annotated with `@Agent` and exposes specific actions through the `@Action` annotation, making them A2A-enabled components that can participate in the agent network.

## Architecture Overview

The application integrates Kafka messaging with Google's A2A protocol using Spring Boot. Here's how it works:

1. **Kafka Message Production**: Messages are sent to three different Kafka topics:
   - orders: For order processing
   - payments: For payment processing
   - alerts: For system alerts

2. **Message Consumption and A2A Integration**: The `SpringKafkaAgent` listens to these topics and converts messages into A2A tasks.

3. **A2A-Enabled Services**: Three services handle different types of messages:
   - OrderService: Processes order-related tasks
   - PaymentService: Handles payment processing
   - AlertService: Manages system alerts

## Key Components

### 1. Main Application

```java
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
```

### 2. Service Implementations

#### Order Service
```java
@Service
@Agent(groupName = "order support", groupDescription = "actions related to order support")
public class OrderService {
    private A2AActionCallBack callBack;

    @Action(description = "Process a new order")
    public String processNewOrder(String orderId, String status, String amount) {
         if(callBack != null) {
           callBack.sendtStatus("Processing Order ID: " + orderId, ActionState.IN_PROGRESS);
           // Process order logic here
           callBack.sendtStatus("Completed Order ID: " + orderId, ActionState.COMPLETED);
         }
         return "Processed order: " + orderId;
    }
}
```

#### Payment Service
```java
@Service
@Agent(groupName = "payment support", groupDescription = "actions related to payment processing")
public class PaymentService {
    @Action(description = "Process a payment")
    public String processPayment(String paymentId, String status, String amount) {
        // Process payment and return result
    }
}
```

#### Alert Service
```java
@Service
@Agent(groupName = "alert support", groupDescription = "actions related to system alerts")
public class AlertService {
    @Action(description = "Process system alert")
    public String processAlert(String alertId, String type, String severity) {
        // Process alert and return result
    }
}
```

### 3. Kafka-A2A Bridge

The `SpringKafkaAgent` class acts as a bridge between Kafka and A2A:

```java
@Component
public class SpringKafkaAgent {
    private final LocalA2ATaskClient client;
    private final ObjectMapper objectMapper;

    private void processMessage(String messageType, String topic, String key, String value) {
        String taskDescription = String.format("kafka-message:%s topic:%s key:%s value:%s",
                messageType, topic, key, value);
        Task task = client.sendTask(taskDescription);
        Task result = client.getTask(task.getId(), 5); // 5-second timeout
    }

    @KafkaListener(topics = "orders", groupId = "a2a-group")
    public void consumeOrderMessages(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            processMessage("order-processing", record.topic(), record.key(), record.value());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing order message", e);
        }
    }

    // Similar listeners for payments and alerts
}
```

## Technical Implementation Details

### 1. Kafka Configuration

```java
@Configuration
@EnableKafka
public class EmbeddedKafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "a2a-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props);
    }
}
```

Key configuration points:
- Uses manual acknowledgment mode for better control over message processing
- Configures both producer and consumer serialization using String serializers
- Sets up consumer group ID for load balancing across multiple instances
- Configures "earliest" offset reset to ensure no messages are missed

### 2. A2A Task Client Integration

Task Processing Details:
- Uses LocalA2ATaskClient for task creation and management
- Implements a 5-second timeout for task completion
- Formats task descriptions with consistent pattern for tracking
- Maintains correlation between Kafka messages and A2A tasks

### 3. Message Flow and Error Handling

1. **Message Reception**:
   ```java
   @KafkaListener(topics = "orders", groupId = "a2a-group")
   public void consumeOrderMessages(ConsumerRecord<String, String> record, Acknowledgment ack) {
       try {
           processMessage("order-processing", record.topic(), record.key(), record.value());
           ack.acknowledge();
       } catch (Exception e) {
           log.error("Error processing order message", e);
       }
   }
   ```

2. **Task State Management**:
   - Tasks can be in states: PENDING, IN_PROGRESS, COMPLETED, FAILED
   - Status updates are sent via callback mechanism
   - Failed tasks don't trigger message redelivery by default

### 4. Service Implementation Details

Service Features:
- Implements status callback for progress tracking
- Uses A2A annotations for service discovery
- Supports synchronous and asynchronous processing
- Provides detailed status updates during task execution

### 5. Performance Considerations

1. **Kafka Configuration**:
   - Configurable batch size for message consumption
   - Tunable consumer thread pool size
   - Adjustable message acknowledgment modes

2. **A2A Task Processing**:
   - Configurable timeout values per task type
   - Support for parallel task processing
   - Built-in task result caching

3. **Scaling Options**:
   - Horizontal scaling through Kafka partitioning
   - Multiple service instances supported
   - Load balancing across consumer group members

### 6. Monitoring and Observability

1. **Kafka Metrics**:
   - Consumer lag monitoring
   - Message throughput tracking
   - Processing time measurements

2. **A2A Task Metrics**:
   - Task completion rates
   - Processing time per task type
   - Error rates and types

3. **Integration Points**:
   - Prometheus metric exports
   - Logging with correlation IDs
   - Distributed tracing support

## How It Works

1. **Message Production**: Messages are produced to Kafka topics using the `KafkaTestProducer`.

2. **Message Consumption**: The `SpringKafkaAgent` listens to these topics using `@KafkaListener` annotations.

3. **A2A Task Creation**: When a message is received:
   - The agent creates an A2A task with the message content
   - The task is sent to the appropriate service based on the message type
   - The service processes the task and returns a result
   - The agent acknowledges the Kafka message

4. **Service Processing**: Each service:
   - Is annotated with `@Agent` to mark it as an A2A agent
   - Has methods annotated with `@Action` to define available actions
   - Processes tasks and returns results
   - Can optionally send status updates using the callback mechanism

## Testing

The application includes integration tests using an embedded Kafka broker:

```java
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"orders", "payments", "alerts"})
public class SpringKafkaAgentTest {
    // Test methods to verify message processing
}
```

## Benefits

1. **Decoupling**: Kafka provides message queuing and A2A provides task processing, creating a well-decoupled system.
2. **Scalability**: Both Kafka and A2A are designed for scalability.
3. **Flexibility**: Easy to add new message types and corresponding services.
4. **Monitoring**: Built-in support for task status tracking and logging.

## Getting Started

1. Ensure you have Java 17+ and Maven installed
2. Clone the repository
3. Run `mvn spring-boot:run`
4. The application will start and process sample messages automatically

## Conclusion

This integration demonstrates how to effectively combine Kafka's messaging capabilities with Google's A2A protocol for building robust, scalable, and maintainable event-driven applications. The combination provides a powerful foundation for building distributed systems that can handle complex workflows and high message volumes while maintaining clear separation of concerns.