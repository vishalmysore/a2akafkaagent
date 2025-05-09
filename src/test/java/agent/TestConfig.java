package agent;

import io.github.vishalmysore.a2a.client.LocalA2ATaskClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {
    @Bean
    public LocalA2ATaskClient localA2ATaskClient() {
        return new LocalA2ATaskClient();
    }
}
