package submission.detail;

import com.redis.testcontainers.RedisContainer;

import submission.core.ports.ICacheFormSchema;

import static org.assertj.core.api.Assertions.assertThat;

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
public class RedisCacheFormSchemaTest {

    @Container
    static RedisContainer redisContainer = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME);

    private RedisTemplate<String, String> redisTemplate;
    private LettuceConnectionFactory connectionFactory;
    private ICacheFormSchema cacheFormSchema;



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

        cacheFormSchema = new RedisFormSchemaCache(redisTemplate);
    }

    @AfterEach
    void tearDown(){
        connectionFactory.destroy();
    }

    @Test
    void SaveFormSchemaById(){
            //Arrange
            String schema = "SCHEMA";
            String schemaId = "Id";

            //Act
            cacheFormSchema.save(schemaId, schema);
            String retrived = cacheFormSchema.getById(schemaId);

            //Assert
            System.out.println("Expected: " + schema);
            System.out.println("Retrieved: " + retrived);
            System.out.println("Match: " + schema.equals(retrived));
            assertThat(retrived).isEqualTo(schema);
    }
}