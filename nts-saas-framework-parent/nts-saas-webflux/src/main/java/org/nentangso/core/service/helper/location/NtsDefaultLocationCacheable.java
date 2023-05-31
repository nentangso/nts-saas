package org.nentangso.core.service.helper.location;

import org.nentangso.core.config.NtsProperties;
import org.nentangso.core.service.dto.NtsDefaultLocationDTO;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.SerializationCodec;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.cache.configuration.Configuration;
import java.util.Map;

@ConditionalOnProperty(
    prefix = "nts.helper.location",
    name = "enabled",
    havingValue = "true"
)
@Component
public class NtsDefaultLocationCacheable {
    private final NtsProperties ntsProperties;
    private final RedissonReactiveClient redissonClient;

    public NtsDefaultLocationCacheable(NtsProperties ntsProperties, Configuration<Object, Object> jcacheConfiguration) {
        this.ntsProperties = ntsProperties;
        redissonClient = ((RedissonConfiguration<?, ?>) jcacheConfiguration).getRedisson().reactive();
    }

    public Mono<Map<Long, NtsDefaultLocationDTO>> getCacheLocations() {
        if (!ntsProperties.getHelper().getLocation().getCache().isEnabled()) {
            return Mono.empty();
        }
        final String cacheKey = generateCacheKey();
        RBucketReactive<Map<Long, NtsDefaultLocationDTO>> bucket = redissonClient.getBucket(cacheKey, new SerializationCodec());
        return bucket.get();
    }

    private String generateCacheKey() {
        return ntsProperties.getHelper().getLocation().getCache().getKeyPrefix() + "locations_by_id";
    }

    public Mono<Map<Long, NtsDefaultLocationDTO>> setCacheLocations(Map<Long, NtsDefaultLocationDTO> items) {
        if (!ntsProperties.getHelper().getLocation().getCache().isEnabled()) {
            return Mono.just(items);
        }
        final String cacheKey = generateCacheKey();
        RBucketReactive<Map<Long, NtsDefaultLocationDTO>> bucket = redissonClient.getBucket(cacheKey, new SerializationCodec());
        return bucket.set(items).thenReturn(items);
    }
}
