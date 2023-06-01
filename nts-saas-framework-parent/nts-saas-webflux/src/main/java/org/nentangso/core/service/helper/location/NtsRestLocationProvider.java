package org.nentangso.core.service.helper.location;

import org.nentangso.core.client.NtsHelperLocationRestClient;
import org.nentangso.core.service.dto.NtsDefaultLocationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NtsRestLocationProvider implements NtsLocationProvider<NtsDefaultLocationDTO> {
    private static final Logger log = LoggerFactory.getLogger(NtsRestLocationProvider.class);

    public static final String PROVIDER_NAME = "org.nentangso.core.service.helper.location.NtsRestLocationProvider";

    private final NtsDefaultLocationCacheable locationCacheable;
    private final NtsHelperLocationRestClient restClient;

    public NtsRestLocationProvider(NtsDefaultLocationCacheable locationCacheable, NtsHelperLocationRestClient restClient) {
        this.locationCacheable = locationCacheable;
        this.restClient = restClient;
    }

    @Override
    public Mono<Map<Long, NtsDefaultLocationDTO>> findAll() {
        return locationCacheable.getCacheLocations()
            .switchIfEmpty(Mono.defer(() -> restClient.findAll()
                .map(items -> items.stream().collect(Collectors.toMap(NtsDefaultLocationDTO::getId, v -> v)))
                .flatMap(locationCacheable::setCacheLocations)));
    }

    @Override
    public Mono<Set<Long>> findAllIds() {
        return findAll()
            .map(Map::keySet);
    }

    @Override
    public Mono<NtsDefaultLocationDTO> findById(final Long id) {
        return findAll()
            .flatMap(f -> Mono.justOrEmpty(f.getOrDefault(id, null)));
    }
}
