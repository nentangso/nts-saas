package org.nentangso.core.service.helper.location;

import org.nentangso.core.config.NtsProperties;
import org.nentangso.core.service.dto.NtsDefaultLocationDTO;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.SerializationCodec;
import org.redisson.jcache.configuration.RedissonConfiguration;
import reactor.core.publisher.Mono;

import javax.cache.configuration.Configuration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NtsDefaultLocationCacheable {
    private final NtsProperties ntsProperties;
    private final RedissonReactiveClient redissonClient;

    public NtsDefaultLocationCacheable(NtsProperties ntsProperties, Configuration<Object, Object> jcacheConfiguration) {
        this.ntsProperties = ntsProperties;
        this.redissonClient = ((RedissonConfiguration<?, ?>) jcacheConfiguration).getRedisson().reactive();
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

    private Long getExpiration() {
        return ntsProperties.getHelper().getLocation().getCache().getExpiration();
    }

    public Mono<Map<Long, NtsDefaultLocationDTO>> setCacheLocations(Map<Long, NtsDefaultLocationDTO> items) {
        if (!ntsProperties.getHelper().getLocation().getCache().isEnabled()) {
            return Mono.just(items);
        }
        final String cacheKey = generateCacheKey();
        RBucketReactive<Map<Long, NtsDefaultLocationDTO>> bucket = redissonClient.getBucket(cacheKey, new SerializationCodec());
        return bucket.set(items, getExpiration(), TimeUnit.SECONDS).thenReturn(items);
    }

    public Mono<Boolean> clearCacheLocations() {
        if (!ntsProperties.getHelper().getLocation().getCache().isEnabled()) {
            return Mono.just(true);
        }
        final String cacheKey = generateCacheKey();
        RBucketReactive<Map<Long, NtsDefaultLocationDTO>> bucket = redissonClient.getBucket(cacheKey, new SerializationCodec());
        return bucket.delete();
    }
}
