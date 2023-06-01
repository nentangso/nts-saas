package org.nentangso.core.service.helper.location;

import org.nentangso.core.client.NtsHelperLocationRestClient;
import org.nentangso.core.service.dto.NtsDefaultLocationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.*;
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
    public Map<Long, NtsDefaultLocationDTO> findAll() {
        Map<Long, NtsDefaultLocationDTO> cacheLocations = locationCacheable.getCacheLocations();
        if (cacheLocations != null && !cacheLocations.isEmpty()) {
            return cacheLocations;
        }
        ResponseEntity<List<NtsDefaultLocationDTO>> response = restClient.findAll();
        if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody()) {
            log.error("Get locations error, response={}", response);
            return Collections.emptyMap();
        }
        Map<Long, NtsDefaultLocationDTO> locations = toLocations(response.getBody());
        locationCacheable.setCacheLocations(locations);
        return locations;
    }

    private static Map<Long, NtsDefaultLocationDTO> toLocations(List<NtsDefaultLocationDTO> items) {
        return Optional.ofNullable(items)
            .orElseGet(Collections::emptyList)
            .stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(NtsDefaultLocationDTO::getId, v -> v));
    }

    @Override
    public Set<Long> findAllIds() {
        return findAll()
            .keySet();
    }

    @Override
    public Optional<NtsDefaultLocationDTO> findById(Long id) {
        NtsDefaultLocationDTO value = findAll()
            .getOrDefault(id, null);
        return Optional.ofNullable(value);
    }
}
