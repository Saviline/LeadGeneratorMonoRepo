package submission.detail;

import com.redis.testcontainers.RedisContainer;

import submission.core.ports.IFormSchemaRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class RedisFormSchemaRepositoryTest {

    @Container
    static RedisContainer redisContainer = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME);

    private RedisTemplate<String, String> redisTemplate;
    private LettuceConnectionFactory connectionFactory;
    private IFormSchemaRepository formSchemaRepository;

    @BeforeEach
    void setUp(){
        String host = redisContainer.getHost();
        int port = redisContainer.getFirstMappedPort();

        connectionFactory = new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
        connectionFactory.afterPropertiesSet();

        redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();

        formSchemaRepository = new RedisFormSchemaRepository(redisTemplate);
    }

    @AfterEach
    void tearDown(){
        connectionFactory.destroy();
    }

    @Test
    void saveAndRetrieveFormSchema(){
        String customerId = "customer-1";
        String schemaId = "schema-1";
        String schema = "{\"fields\":[]}";

        formSchemaRepository.save(customerId, schemaId, schema);
        Optional<String> retrieved = formSchemaRepository.findByCustomerIdAndSchemaId(customerId, schemaId);

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(schema);
    }

    @Test
    void findByCustomerIdAndSchemaId_NotFound_ReturnsEmpty(){
        Optional<String> retrieved = formSchemaRepository.findByCustomerIdAndSchemaId("unknown", "unknown");

        assertThat(retrieved).isEmpty();
    }
}
