package org.nentangso.core.service.helper.location;

import org.nentangso.core.config.NtsProperties;
import org.nentangso.core.service.dto.NtsDefaultLocationDTO;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.redisson.jcache.configuration.RedissonConfiguration;

import javax.cache.configuration.Configuration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NtsDefaultLocationCacheable {
    private final NtsProperties ntsProperties;
    private final RedissonClient redissonClient;

    public NtsDefaultLocationCacheable(NtsProperties ntsProperties, Configuration<Object, Object> jcacheConfiguration) {
        this.ntsProperties = ntsProperties;
        this.redissonClient = ((RedissonConfiguration<?, ?>) jcacheConfiguration).getRedisson();
    }

    public Map<Long, NtsDefaultLocationDTO> getCacheLocations() {
        if (!ntsProperties.getHelper().getLocation().getCache().isEnabled()) {
            return null;
        }
        final String cacheKey = generateCacheKey();
        RBucket<Map<Long, NtsDefaultLocationDTO>> bucket = redissonClient.getBucket(cacheKey, new SerializationCodec());
        return bucket.get();
    }

    private Long getExpiration() {
        return ntsProperties.getHelper().getLocation().getCache().getExpiration();
    }

    private String generateCacheKey() {
        return ntsProperties.getHelper().getLocation().getCache().getKeyPrefix() + "locations_by_id";
    }

    public void setCacheLocations(Map<Long, NtsDefaultLocationDTO> items) {
        if (!ntsProperties.getHelper().getLocation().getCache().isEnabled()) {
            return;
        }
        final String cacheKey = generateCacheKey();
        RBucket<Map<Long, NtsDefaultLocationDTO>> bucket = redissonClient.getBucket(cacheKey, new SerializationCodec());
        bucket.set(items, getExpiration(), TimeUnit.SECONDS);
    }

    public boolean clearCacheLocations() {
        if (!ntsProperties.getHelper().getLocation().getCache().isEnabled()) {
            return true;
        }
        final String cacheKey = generateCacheKey();
        RBucket<Map<Long, NtsDefaultLocationDTO>> bucket = redissonClient.getBucket(cacheKey, new SerializationCodec());
        return bucket.delete();
    }
}
