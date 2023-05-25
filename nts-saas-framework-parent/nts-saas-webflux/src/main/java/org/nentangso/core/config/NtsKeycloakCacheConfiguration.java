package org.nentangso.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.nentangso.core.service.dto.NtsDefaultLocationDTO;
import org.nentangso.core.service.provider.NtsKeycloakLocationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;
import java.util.Set;

@ConditionalOnProperty(
    prefix = "nts.helper.location",
    name = "provider",
    havingValue = NtsKeycloakLocationProvider.PROVIDER_NAME
)
@Configuration
public class NtsKeycloakCacheConfiguration {
    @Bean
    ReactiveRedisOperations<String, Map<Long, NtsDefaultLocationDTO>> ntsLocationsOps(ReactiveRedisConnectionFactory factory, ObjectMapper objectMapper) {
        MapType type = MapType.construct(
            Map.class,
            TypeBindings.create(Set.class, SimpleType.constructUnsafe(NtsDefaultLocationDTO.class)),
            TypeFactory.unknownType(),
            null,
            SimpleType.constructUnsafe(Long.class),
            SimpleType.constructUnsafe(NtsDefaultLocationDTO.class)
        );
        Jackson2JsonRedisSerializer<Map<Long, NtsDefaultLocationDTO>> serializer = new Jackson2JsonRedisSerializer<>(type);
        serializer.setObjectMapper(objectMapper);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Map<Long, NtsDefaultLocationDTO>> builder = RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Map<Long, NtsDefaultLocationDTO>> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
